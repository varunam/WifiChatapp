package com.lib.serialcommunicator;

import com.lib.serialcommunicator.interfaces.ClientMessageReceivedCallbacks;
import com.lib.serialcommunicator.interfaces.ServerConnectedCallbacks;
import com.lib.serialcommunicator.interfaces.SocketsClosedCallbacks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by varun.am on 24/10/18
 */
public class ClientReceiver extends Thread {

    private static final String TAG = ClientReceiver.class.getSimpleName();
    private static Socket clientSocket;
    private String serverIpAddress;
    private int port;
    private ClientMessageReceivedCallbacks clientCallbacks;
    private ServerConnectedCallbacks serverConnectedCallbacks;
    private Logger Log = Logger.getLogger(TAG);

    public ClientReceiver(String serverIp, int port, ClientMessageReceivedCallbacks clientCallbacks, ServerConnectedCallbacks serverConnectedCallbacks) {
        this.clientCallbacks = clientCallbacks;
        this.serverIpAddress = serverIp;
        this.port = port;
        this.serverConnectedCallbacks = serverConnectedCallbacks;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Log.log(Level.INFO, "Connecting to server at " + port);
                clientSocket = new Socket(serverIpAddress, port);
                if (serverConnectedCallbacks != null)
                    serverConnectedCallbacks.connectionToServerSuccess(port);
                listenToSocket(clientSocket);
            } catch (IOException e) {
                Log.log(Level.SEVERE, "Couldn't connect to serverSocket");
                e.printStackTrace();
                if (serverConnectedCallbacks != null)
                    serverConnectedCallbacks.connectionToServerFailure(e.getMessage());
                return;
            }
        }
    }

    private void listenToSocket(Socket clientSocket) {
        Log.log(Level.INFO, "Established connection to " + serverIpAddress + ":" + port + "\nListening...");
        while (true) {
            try {
                String message = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
                if (message == null) {
                    Log.log(Level.SEVERE, "received NULL from serversocket. returning...");
                    return;
                } else {
                    clientCallbacks.onMessageReceivedByClient(message);
                    Log.log(Level.INFO, "Received message from ServerSocket: " + message);
                }
            } catch (Exception e) {
                clientCallbacks.onMessageReceiveFailure();
                Log.log(Level.SEVERE, "Couldn't receive message");
                e.printStackTrace();
            }
        }
    }

    public static void closeSockets(SocketsClosedCallbacks socketsClosedCallbacks) {
        //closing socket
        Logger.getLogger(TAG).log(Level.INFO, "Closing socket");
        try {
            clientSocket.close();
            if (socketsClosedCallbacks != null) {
                Logger.getLogger(TAG).log(Level.INFO, "Closed clientSocket successfully");
                socketsClosedCallbacks.socketCloseSuccessful();
            }
        } catch (IOException e) {
            if (socketsClosedCallbacks != null) {
                Logger.getLogger(TAG).log(Level.SEVERE, "Couldn't close clientSocket " + e);
                socketsClosedCallbacks.socketCloseFailure();
            }

            Logger.getLogger(TAG).log(Level.SEVERE, "Couldn't close socket " + e);
            e.printStackTrace();
        }
    }
}
