package com.lib.serialcommunicator.interfaces;

/**
 * Created by varun.am on 27/10/18
 */
public interface ServerMessageSentCallbacks {
    public void serverMessageSendSuccessfull(String sentMessage);
    public void serverMessageSendFailure(String failureReason);
}
