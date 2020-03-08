package com.educarea.mobile.internet;

import android.util.Log;

import java.net.URI;
import java.util.concurrent.TimeUnit;

public class InetWorker implements MessageListener {
    private WebSocketWorker webSocketWorker = null;
    private MessageListener messageListener = null;
    private URI uri = null;
    private boolean offlineMode = false;
    private static final Object lockConnect = new Object();

    public InetWorker(URI uri) {
        this.uri = uri;
    }

    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }


    public synchronized boolean connectBlocking(){
        if (offlineMode) return false;
        boolean status;
        if (webSocketWorker!=null){
            if (webSocketWorker.isConnected()){
                closeConnection();
            }
        }
        webSocketWorker = new WebSocketWorker(uri);
        try{
            status = webSocketWorker.connectBlocking(3,TimeUnit.SECONDS);
            if (status){
                webSocketWorker.setMessageListener(InetWorker.this);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
            return false;
        }
        return status;
    }

    public void connect(){
        if (offlineMode) return;
        class NetConnector implements Runnable{
            private boolean status = false;
            @Override
            public void run() {
                synchronized (lockConnect) {
                    if (webSocketWorker != null) {
                        if (webSocketWorker.isConnected()) {
                            return;
                        }
                    }
                    webSocketWorker = new WebSocketWorker(uri);
                    try {
                        status = webSocketWorker.connectBlocking(5, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (status) {
                        webSocketWorker.setMessageListener(InetWorker.this);
                        messageListener.messageIncome(CONNECT_DONE);
                    } else {
                        messageListener.messageIncome(NO_CONNECTION);
                    }
                }
            }
        }

        new Thread(new NetConnector()).start();
    }

    public void setOfflineMode(boolean offlineMode) {
        this.offlineMode = offlineMode;
        closeConnection();
    }

    public boolean isOfflineMode() {
        return offlineMode;
    }

    public synchronized void closeConnection(){
        if (webSocketWorker != null){
            try {
                webSocketWorker.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        webSocketWorker = null;
    }

    public boolean isConnected(){
        if (webSocketWorker == null){
            return false;
        }else {
            return webSocketWorker.isConnected();
        }
    }

    public void send(String data){

        if (offlineMode){
            try{
                messageListener.messageIncome(MessageListener.OFFLINE_MODE);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            return;
        }
        class MessageSender implements Runnable {
            private String message;


            public MessageSender(String message) {
                this.message = message;

            }

            @Override
            public void run(){
                if (!webSocketWorker.isConnected()){
                    try{
                        messageListener.messageIncome(MessageListener.NO_CONNECTION);
                    }catch (NullPointerException e){
                        e.printStackTrace();
                    }
                }else {
                    try {
                        webSocketWorker.send(message);
                    } catch (Exception e) {
                        try {
                            messageListener.messageIncome(MessageListener.ERROR);
                        } catch (NullPointerException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }

        Thread thread = new Thread(new MessageSender(data));
        thread.start();
    }

    @Override
    public void messageIncome(String message) {
        try {
            messageListener.messageIncome(message);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

}
