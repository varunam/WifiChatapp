package com.lib.serialcommunicator.interfaces;

import java.net.Socket;

/**
 * Created by varun.am on 24/10/18
 */
public interface ClientConnectedCallbacks {
    public void onClientConnected(Socket clientSocket, int serverPortOccupied);
    public void onClientConnectionFailure(String failureReason);
}
