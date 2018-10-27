package com.lib.serialcommunicator;

import com.lib.serialcommunicator.interfaces.ClientConnectedCallbacks;
import com.lib.serialcommunicator.interfaces.ServerMessageReceivedCallbacks;
import com.lib.serialcommunicator.interfaces.ServerSocketCreationCallbacks;
import com.lib.serialcommunicator.interfaces.SocketsClosedCallbacks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by varun.am on 24/10/18
 */
public class ServerReceiver extends Thread {

    private static final String TAG = ServerReceiver.class.getSimpleName();
    private static ServerSocket serverSocket;
    private int port;
    private Logger Log = Logger.getLogger(TAG);
    private ServerMessageReceivedCallbacks serverMessageReceivedCallbacks;
    private ClientConnectedCallbacks clientConnectedCallbacks;
    private ServerSocketCreationCallbacks serverSocketCreationCallbacks;
    private static Socket socket;

    public ServerReceiver(int port, ServerSocketCreationCallbacks serverSocketCreationCallbacks, ServerMessageReceivedCallbacks serverMessageReceivedCallbacks) {
        this.serverSocketCreationCallbacks = serverSocketCreationCallbacks;
        this.serverMessageReceivedCallbacks = serverMessageReceivedCallbacks;
        this.port = port;
    }

    public ServerReceiver(int port, ServerSocketCreationCallbacks serverSocketCreationCallbacks, ServerMessageReceivedCallbacks serverMessageReceivedCallbacks, ClientConnectedCallbacks clientConnectedCallbacks) {
        this.serverSocketCreationCallbacks = serverSocketCreationCallbacks;
        this.port = port;
        this.serverMessageReceivedCallbacks = serverMessageReceivedCallbacks;
        this.clientConnectedCallbacks = clientConnectedCallbacks;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            if (serverSocketCreationCallbacks != null) {
                Log.log(Level.INFO, "ServerSocket created successfully, port: " + port);
                serverSocketCreationCallbacks.serverSocketCreationSuccessful();
            }
            while (true) {
                try {
                    if (serverSocket != null) {
                        Log.log(Level.INFO, "Waiting for client at " + port);
                        socket = serverSocket.accept();
                        listenToSocket(socket);
                        if (clientConnectedCallbacks != null) {
                            clientConnectedCallbacks.onClientConnected(socket);
                        }
                        Log.log(Level.INFO, "Client connected: " + socket.getInetAddress() + ":" + socket.getPort());
                    } else break;
                } catch (IOException e) {
                    if (clientConnectedCallbacks != null) {
                        clientConnectedCallbacks.onClientConnectionFailure(e.getMessage());
                    }
                    Log.log(Level.SEVERE, "Couldn't accept socket connection");
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    Log.log(Level.SEVERE, "ServerSocket is null");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            if (serverSocketCreationCallbacks != null) {
                Log.log(Level.INFO, "ServerSocket creation failure, port: " + port);
                serverSocketCreationCallbacks.serverSocketCreationFailure(e.getMessage());
            }
            Log.log(Level.SEVERE, "Couldn't create serverSocket");
            e.printStackTrace();
        }
    }

    private void listenToSocket(final Socket socket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.log(Level.INFO, "Waiting for message...");
                while (true) {
                    String message = receiveMessage(socket);
                    if (message == null)
                    {
                        Log.log(Level.INFO,"Message received is NULL. Returning...");
                        return;
                    }
                    else {
                        Log.log(Level.INFO, "Received message from ServerSocket: " + message);
                        serverMessageReceivedCallbacks.onMessageReceivedByServer(message);
                    }
                }
            }
        }).start();
    }

    private String receiveMessage(Socket socket) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return bufferedReader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void closeSockets(SocketsClosedCallbacks socketsClosedCallbacks) {
        try {
            serverSocket.close();
            serverSocket = null;
            socket.close();
            if (socketsClosedCallbacks != null) {
                Logger.getLogger(TAG).log(Level.INFO, "Closed serverSocket and clientSocket connected successfully");
                socketsClosedCallbacks.socketCloseSuccessful();
            }
        } catch (IOException e) {
            if (socketsClosedCallbacks != null) {
                Logger.getLogger(TAG).log(Level.SEVERE, "Couldn't close serverSocket and clientSocket" + e);
                socketsClosedCallbacks.socketCloseFailure();
            }

            e.printStackTrace();
        }
    }

}
