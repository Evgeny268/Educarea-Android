package com.educarea.mobile.internet;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebSocketWorker extends WebSocketClient {

    private boolean connected = false;
    private MessageListener messageListener = null;

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public WebSocketWorker(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        connected = true;
        Log.d("educarea","open websocket connection");
    }

    @Override
    public void onMessage(String message) {
        Log.d("educarea","new message: "+message);
        try {
            messageListener.messageIncome(message);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        connected = false;
        try {
            messageListener.messageIncome(MessageListener.CLOSING);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        messageListener = null;
        Log.d("educarea","close websocket connection");
    }

    @Override
    public void onError(Exception ex) {
        Log.d("educarea","websocket error");
        ex.printStackTrace();
        try {
            messageListener.messageIncome(MessageListener.ERROR);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
