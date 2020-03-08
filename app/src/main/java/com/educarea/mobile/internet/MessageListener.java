package com.educarea.mobile.internet;

public interface MessageListener {
    String RECONNECT = "RECONNECT";
    String CONNECT_DONE = "CONNECT_DONE";
    String NO_CONNECTION = "NO_CONNECTION";
    String CLOSING = "CLOSING";
    String ERROR = "ERROR";
    String OFFLINE_MODE = "OFFLINE_MODE";
    void messageIncome(String message);
}
