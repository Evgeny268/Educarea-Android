package com.educarea.mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.educarea.mobile.dialogs.TwoAnswerAlertDialog;
import com.educarea.mobile.internet.MessageListener;

import transfers.Authorization;
import transfers.TransferRequestAnswer;
import transfers.Transfers;
import transfers.TransfersFactory;
import transfers.TypeRequestAnswer;
import transfers.User;
import transfers.VersionInfo;

public class MainActivity extends AppInetActivity implements TypeRequestAnswer, MessageListener, TwoAnswerAlertDialog.TwoAnswerClickListener {

    private TextView textVersion;
    private Button buttonReconnect;
    private Button ofllineMode;
    private VersionInfo versionInfo;
    private TwoAnswerAlertDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eduApp.getAppData().loadData(this);
        textVersion = findViewById(R.id.textViewVersion);
        textVersion.setText(getResources().getString(R.string.app_version)+" "+BuildConfig.VERSION_NAME);
        buttonReconnect = findViewById(R.id.buttonReconnect);
        ofllineMode = findViewById(R.id.buttonEnableOffline);
        if (eduApp.getInetWorker()==null) {
            eduApp.initInetWorker();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!eduApp.getInetWorker().isConnected()){
            eduApp.getInetWorker().connect();
        }else {
            eduApp.sendTransfers(new TransferRequestAnswer(GET_PLATFORM_VERSION, VersionInfo.PLATFORM_ANDROID));
        }
    }

    @Override
    protected void newMessage(String message) {
        if (message.equals(CONNECT_DONE)){
            eduApp.sendTransfers(new TransferRequestAnswer(GET_PLATFORM_VERSION, VersionInfo.PLATFORM_ANDROID));
        }else if (message.equals(NO_CONNECTION)){
            Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            buttonReconnect.setVisibility(View.VISIBLE);
            buttonReconnect.setEnabled(true);
            ofllineMode.setVisibility(View.VISIBLE);
            ofllineMode.setEnabled(true);
        }else {
            String data = message;
            Transfers in = TransfersFactory.createFromJSON(data);
            if (in != null) {
                if (in instanceof TransferRequestAnswer) {
                    if (((TransferRequestAnswer) in).request.equals(AUTHORIZATION_DONE)) {
                        User user = new User(Integer.parseInt(((TransferRequestAnswer) in).extra), ((TransferRequestAnswer) in).extraArr[0]);
                        eduApp.getAppData().setUser(user,this);
                        startActivity(new Intent(MainActivity.this, GroupsListActivity.class));
                    } else if (((TransferRequestAnswer) in).request.equals(LOGOUT)) {
                        eduApp.saveToken("");
                        Intent intent = new Intent(MainActivity.this, LogRegActivity.class);
                        startActivity(intent);
                    } else {
                        eduApp.standartReactionOnAsnwer(data, MainActivity.this);
                    }
                } else if (in instanceof VersionInfo) {
                    versionInfo = (VersionInfo) in;
                    int currentVersion = BuildConfig.VERSION_CODE;
                    if (currentVersion < versionInfo.lastVersionId) {
                        showUpdateDialog();
                    } else {
                        appEnter();
                    }
                } else {
                    eduApp.standartReactionOnAsnwer(data, MainActivity.this);
                }
            } else {
                eduApp.standartReactionOnAsnwer(data, MainActivity.this);
            }
        }
    }


    public void onClickReconnect(View view) {
        buttonReconnect.setEnabled(false);
        buttonReconnect.setVisibility(View.GONE);
        ofllineMode.setEnabled(false);
        ofllineMode.setVisibility(View.GONE);
        if (eduApp.getInetWorker()==null){
            eduApp.initInetWorker();
        }
        if (!eduApp.getInetWorker().isConnected()){
            eduApp.getInetWorker().connect();
        }
    }

    @Override
    public void onClickFirstButton() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(versionInfo.updateLink));
        startActivity(browserIntent);
    }

    @Override
    public void onClickSecondButton() {
        int currentVersion = BuildConfig.VERSION_CODE;
        if (currentVersion<versionInfo.supportedVersionId){
            eduApp.getInetWorker().setOfflineMode(true);
            startActivity(new Intent(MainActivity.this, GroupsListActivity.class));
        }else {
            appEnter();
        }
    }

    private void showUpdateDialog(){
        if (dialog!=null){
            if (dialog.isAdded()){
                return;
            }
        }
        int currentVersion = BuildConfig.VERSION_CODE;
        if (currentVersion < versionInfo.supportedVersionId) {
            dialog = new TwoAnswerAlertDialog(this, getString(R.string.update_available)+" v: "+versionInfo.lastVersionName,
                    getString(R.string.need_update), getString(R.string.download), getString(R.string.offline_mode));
            FragmentManager manager = getSupportFragmentManager();
            dialog.setCancelable(false);
            dialog.show(manager, "NEED_UPDATE");
        } else {
            String textMssage = getString(R.string.update_available) + " (" +getString(R.string.app_version)+" "+ versionInfo.lastVersionName + ") " + getString(R.string.update_ask);
            dialog = new TwoAnswerAlertDialog(this, getString(R.string.update_available),
                    textMssage, getString(R.string.download), getString(R.string.later));
            FragmentManager manager = getSupportFragmentManager();
            dialog.setCancelable(false);
            dialog.show(manager, "CAN_UPDATE");
        }
    }


    private void appEnter(){
        String auth_token = eduApp.getUser_token();
        eduApp.getInetWorker().setMessageListener(MainActivity.this);
        if (auth_token==null){
            Intent intent = new Intent(MainActivity.this,LogRegActivity.class);
            startActivity(intent);
        }else {
            Authorization out = new Authorization(auth_token);
            eduApp.sendTransfers(out);
        }
    }

    public void onClickOffline(View view) {
        eduApp.getInetWorker().setOfflineMode(true);
        startActivity(new Intent(MainActivity.this, GroupsListActivity.class));
    }
}
