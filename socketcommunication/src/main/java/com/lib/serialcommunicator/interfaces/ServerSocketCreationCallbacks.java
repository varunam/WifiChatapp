package com.lib.serialcommunicator.interfaces;

/**
 * Created by varun.am on 27/10/18
 */
public interface ServerSocketCreationCallbacks {
    public void serverSocketCreationSuccessful();
    public void serverSocketCreationFailure(String failureReason);
}
