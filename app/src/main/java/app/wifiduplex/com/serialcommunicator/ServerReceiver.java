package app.wifiduplex.com.serialcommunicator;

import app.wifiduplex.com.serialcommunicator.interfaces.ClientConnectedCallbacks;
import app.wifiduplex.com.serialcommunicator.interfaces.ServerMessageReceivedCallbacks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by varun.am on 24/10/18
 */
public class ServerReceiver extends Thread {

    private static final String TAG = ServerReceiver.class.getSimpleName();
    private ServerSocket serverSocket;
    private int port;
    private Logger Log = Logger.getLogger(TAG);
    private boolean endConnection = false;
    private ServerMessageReceivedCallbacks serverMessageReceivedCallbacks;
    private ClientConnectedCallbacks clientConnectedCallbacks;
    private Socket socket;

    public ServerReceiver(int port, ServerMessageReceivedCallbacks serverMessageReceivedCallbacks) {
        this.serverMessageReceivedCallbacks = serverMessageReceivedCallbacks;
        this.port = port;
    }

    public ServerReceiver(int port, ServerMessageReceivedCallbacks serverMessageReceivedCallbacks, ClientConnectedCallbacks clientConnectedCallbacks) {
        this.port = port;
        this.serverMessageReceivedCallbacks = serverMessageReceivedCallbacks;
        this.clientConnectedCallbacks = clientConnectedCallbacks;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                try {
                    Log.log(Level.INFO, "Waiting for client...");
                    socket = serverSocket.accept();
                    listenToSocket(socket);
                    if (clientConnectedCallbacks != null) {
                        clientConnectedCallbacks.onClientConnected(socket.getInetAddress().toString(), socket.getPort());
                    }
                    Log.log(Level.INFO, "Client connected with ip: " + socket.getInetAddress() + " at port: " + socket.getPort());
                } catch (IOException e) {
                    if (clientConnectedCallbacks != null) {
                        clientConnectedCallbacks.onClientConnectionFailure();
                    }
                    Log.log(Level.SEVERE, "Couldn't accept socket connection");
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    Log.log(Level.SEVERE, "ServerSocket is null");
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            Log.log(Level.SEVERE, "Couldn't create serverSocket");
            e.printStackTrace();
        }
    }

    private void listenToSocket(final Socket socket) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.log(Level.INFO, "Waiting for message...");
                while (!endConnection) {
                    String message = receiveMessage(socket);
                    if (message == null)
                        return;
                    message = message + "\n Received: " + new Date() + "\n";
                    Log.log(Level.INFO, "Received message from ServerSocket: " + message);
                    serverMessageReceivedCallbacks.onMessageReceivedByServer(message);
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
}
