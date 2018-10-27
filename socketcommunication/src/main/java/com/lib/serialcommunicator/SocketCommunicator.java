package com.lib.serialcommunicator;


import com.lib.serialcommunicator.interfaces.*;

import java.net.Socket;
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

    /**
     * Use this method to send message to client connected.
     * Note: The connection should be live
     *
     * @param clientSocket - implement callbacks required for method "listenToClient" and implement clientConnectionSuccessful(Socket clientSocket) and use the clientSocket to perform this action
     * @param message      - message to be sent to client
     * @param serverMessageSentCallbacks - callbacks for message sent success and failure
     *
     */
    public void sendMessageToClient(Socket clientSocket, String message, ServerMessageSentCallbacks serverMessageSentCallbacks) {
        Log.log(Level.INFO, "Request received: Send message to client: " + clientSocket.getInetAddress() + " at port: " + clientSocket.getPort());
        new Thread(new ServerSender(clientSocket, message, serverMessageSentCallbacks)).start();
    }

    public void listenToServer(String serverIpAddress, int port, ClientMessageReceivedCallbacks clientMessageReceivedCallbacks, ServerConnectedCallbacks serverConnectedCallbacks) {
        Log.log(Level.INFO, "Request received: Listen to server: " + serverIpAddress + " at port: " + port);
        new Thread(new ClientReceiver(serverIpAddress, port, clientMessageReceivedCallbacks, serverConnectedCallbacks)).start();
    }

    public void listenToClient(int[] listeningPort, ServerSocketCreationCallbacks serverSocketCreationCallbacks, ServerMessageReceivedCallbacks serverMessageReceivedCallbacks, ClientConnectedCallbacks clientConnectedCallbacks) {
        for (int i = 0; i < listeningPort.length; i++) {
            Log.log(Level.INFO, "Request received: Listen to clients at " + listeningPort[i]);
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
