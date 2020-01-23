package com.educarea.mobile;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.educarea.mobile.internet.InetWorker;
import com.educarea.mobile.internet.MessageListener;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;

public class EduApp extends Application implements TypeRequestAnswer {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_USER_TOKEN = "usertoken";
    public static final String APP_USER_CLOUD_TOKEN = "usercloudtoken";
    private String user_token = null;
    private InetWorker inetWorker = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("educarea","EduApp onCreate");
        loadToken();
    }

    public boolean initInternet(){
        if (inetWorker == null) {
            URI uri = null;
            try {
                uri = new URI(getString(R.string.server_address));
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return false;
            }
            inetWorker = new InetWorker(uri);
        }
        if (!inetWorker.isConnected()){
            if (inetWorker.connectBlocking()){
                return true;
            }else {
                return false;
            }
        }else {
            return true;
        }
    }

    public InetWorker getInetWorker() {
        return inetWorker;
    }

    public String getUser_token() {
        return user_token;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public boolean sendTransfers(Transfers transfers){
        try {
            String out = objToJson(transfers);
            inetWorker.send(out);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String objToJson(Object c) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        StringWriter stringWriter = new StringWriter();
        objectMapper.writeValue(stringWriter,c);
        return stringWriter.toString();
    }

    public void standartReactionOnAsnwer(String data, Context context){
        if (data==null) return;
        if (data.equals(MessageListener.RECONNECT)){
            Toast.makeText(context, context.getString(R.string.reconnect_to_network), Toast.LENGTH_SHORT).show();
        }else if (data.equals(MessageListener.NO_CONNECTION) || data.equals(MessageListener.CLOSING)){
            Toast.makeText(context, context.getString(R.string.reconnect_to_network), Toast.LENGTH_SHORT).show();
            inetWorker.connect();
        }else if (data.equals(MessageListener.CONNECT_DONE)){
            Toast.makeText(context, context.getString(R.string.connected), Toast.LENGTH_SHORT).show();
        }else if (data.equals(MessageListener.ERROR)){
            Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show();
        }else if (data.equals(MessageListener.CLOSING)){
            Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }else if (data.equals(LOGOUT)){
            clearAllUserData();
            context.startActivity(new Intent(context,MainActivity.class));
        }
        else {
            Transfers input = TransfersFactory.createFromJSON(data);
            if (input!=null){
                if (input instanceof TransferRequestAnswer){
                    if (((TransferRequestAnswer) input).request.equals(BAD_LOGIN)){
                        Toast.makeText(context, context.getString(R.string.badLogin), Toast.LENGTH_SHORT).show();
                    }else if (((TransferRequestAnswer) input).request.equals(BAD_PASSWORD)){
                        Toast.makeText(context, context.getString(R.string.badPassword), Toast.LENGTH_SHORT).show();
                    }else if (((TransferRequestAnswer) input).request.equals(USER_ALREADY_EXIST)){
                        Toast.makeText(context, context.getString(R.string.userAlreadyExist), Toast.LENGTH_SHORT).show();
                    }else if (((TransferRequestAnswer) input).request.equals(USER_NOT_EXIST)){
                        Toast.makeText(context, context.getString(R.string.userNotExist), Toast.LENGTH_SHORT).show();
                    }else if (((TransferRequestAnswer) input).request.equals(WRONG_PASSWORD)){
                        Toast.makeText(context, context.getString(R.string.wrongPassword), Toast.LENGTH_SHORT).show();
                    }else if (((TransferRequestAnswer) input).request.equals(ERROR)){
                        Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    public void saveToken(String token){
        SharedPreferences mSetting = getApplicationContext().getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor editor = mSetting.edit();
        editor.putString(APP_USER_TOKEN,token);
        editor.apply();
        user_token = token;
    }

    public void loadToken(){
        SharedPreferences mSetting = getApplicationContext().getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE);
        if (mSetting.contains(APP_USER_TOKEN)){
            user_token = mSetting.getString(APP_USER_TOKEN,"");
            if (user_token.equals("")){
                user_token = null;
            }
        }
    }

    public void clearAllUserData(){
        SharedPreferences mSetting = getApplicationContext().getSharedPreferences(APP_PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor editor = mSetting.edit();
        editor.clear();
        editor.apply();
    }


}
