package com.lib.socketcommunication.interfaces;

/**
 * Created by varun.am on 24/10/18
 */
public interface ClientMessageSentCallbacks {

    public void clientMessageSentSuccessful(String messageSent);
    public void clientMessageSendFailure();

}
