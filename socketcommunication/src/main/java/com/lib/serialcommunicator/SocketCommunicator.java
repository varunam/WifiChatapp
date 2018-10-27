package com.lib.serialcommunicator;


import com.lib.serialcommunicator.interfaces.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by varun.am on 25/10/18
 */
public class SocketCommunicator {

    private static final String TAG = SocketCommunicator.class.getSimpleName();
    private Logger Log = Logger.getLogger(TAG);

    public void sendMessageToServer(String ipAddress, int port, String message, ClientMessageSentCallbacks clientMessageSentCallbacks) {
        Log.log(Level.INFO, "\" Request received: Send message \"" + message + "\" to server: " + ipAddress + " at port: " + port);
        new Thread(new ClientSender(ipAddress, port, message, clientMessageSentCallbacks)).start();
    }

    public void listenToServer(String serverIpAddress, int port, ClientMessageReceivedCallbacks clientMessageReceivedCallbacks) {
        Log.log(Level.INFO, "\" Request received: Listen to server: " + serverIpAddress + " at port: " + port);
        new Thread(new ClientReceiver(serverIpAddress, port, clientMessageReceivedCallbacks)).start();
    }

    public void listenToClient(int[] listeningPort, ServerSocketCreationCallbacks serverSocketCreationCallbacks, ServerMessageReceivedCallbacks serverMessageReceivedCallbacks, ClientConnectedCallbacks clientConnectedCallbacks) {
        for (int i = 0; i < listeningPort.length; i++) {
            Log.log(Level.INFO, "\" Request received: Listen to clients at " + listeningPort[i]);
            new Thread(new ServerReceiver(listeningPort[i], serverSocketCreationCallbacks, serverMessageReceivedCallbacks, clientConnectedCallbacks)).start();
        }
    }

    public void stopListeningtoClient(SocketsClosedCallbacks socketsClosedCallbacks) {
        Log.log(Level.INFO, "Request received: Stop listening to client");
        ServerReceiver.closeSockets(socketsClosedCallbacks);
    }

    public void stopListeningToServer(SocketsClosedCallbacks socketsClosedCallbacks) {
        Log.log(Level.INFO, "Request received: Stop listen to server");
        ClientReceiver.closeSockets(socketsClosedCallbacks);
    }

}
