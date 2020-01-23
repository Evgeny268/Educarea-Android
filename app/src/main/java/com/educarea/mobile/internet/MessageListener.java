package com.educarea.mobile.internet;

public interface MessageListener {
    String RECONNECT = "RECONNECT";
    String CONNECT_DONE = "CONNECT_DONE";
    String NO_CONNECTION = "NO_CONNECTION";
    String CLOSING = "CLOSING";
    String ERROR = "ERROR";
    void messageIncome(String message);
}
