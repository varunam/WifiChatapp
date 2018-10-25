package com.lib.socketcommunication;

import com.lib.socketcommunication.interfaces.ClientMessageReceivedCallbacks;

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
    private Socket clientSocket;
    private String serverIpAddress;
    private int port;
    private boolean endConnection = false;
    private ClientMessageReceivedCallbacks clientCallbacks;
    private Logger Log = Logger.getLogger(TAG);

    public ClientReceiver(String serverIp, int port, ClientMessageReceivedCallbacks clientCallbacks) {
        this.clientCallbacks = clientCallbacks;
        this.serverIpAddress = serverIp;
        this.port = port;
    }

    @Override
    public void run() {
        while (true) {
            try {
                clientSocket = new Socket(serverIpAddress, port);
                listenToSocket(clientSocket);
                Log.log(Level.INFO, "Established connection to " + serverIpAddress + " \nListening...");
            } catch (IOException e) {
                Log.log(Level.SEVERE, "Couldn't connect to serverSocket");
                e.printStackTrace();
            }
        }
    }

    private void listenToSocket(Socket clientSocket) {
        while (!endConnection) {
            try {
                String message = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())).readLine();
                clientCallbacks.onMessageReceivedByClient(message);
                Log.log(Level.INFO, "Received message from ServerSocket: " + message);
            } catch (Exception e) {
                clientCallbacks.onMessageReceiveFailure();
                Log.log(Level.SEVERE, "Couldn't receive message");
                e.printStackTrace();
            }
        }
    }
}
