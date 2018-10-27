package com.lib.serialcommunicator.interfaces;

/**
 * Created by varun.am on 27/10/18
 */
public interface ServerConnectedCallbacks {
    public void connectionToServerSuccess(int connectedServerPort);
    public void connectionToServerFailure(String failureReason);
}
