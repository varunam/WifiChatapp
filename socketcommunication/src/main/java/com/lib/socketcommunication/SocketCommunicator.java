package com.lib.socketcommunication;

import com.lib.socketcommunication.interfaces.ClientConnectedCallbacks;
import com.lib.socketcommunication.interfaces.ClientMessageReceivedCallbacks;
import com.lib.socketcommunication.interfaces.ClientMessageSentCallbacks;
import com.lib.socketcommunication.interfaces.ServerMessageReceivedCallbacks;

import java.util.logging.Logger;

public class SocketCommunicator {

    private static final String TAG = SocketCommunicator.class.getSimpleName();
    private Logger Log = Logger.getLogger(TAG);
    private ClientMessageReceivedCallbacks clientMessageCallbacks;
    private ClientConnectedCallbacks clientConnectedCallbacks;
    private ServerMessageReceivedCallbacks serverMessageReceivedCallbacks;

    private enum DeviceType {
        SERVER,
        CLIENT
    }

    private DeviceType deviceType;
    private String serverIpAddress;
    private int serverPort;

    public void listenToServer(String serverIp, int port, ClientMessageReceivedCallbacks clientCallbacks) {
        this.clientMessageCallbacks = clientCallbacks;
        this.deviceType = DeviceType.CLIENT;
        this.serverIpAddress = serverIp;
        this.serverPort = port;
        Thread clientThread = new Thread(new ClientReceiver(serverIp, port, clientMessageCallbacks));
        clientThread.start();
    }

    public void listenToClient(int port, ServerMessageReceivedCallbacks serverMessageReceivedCallbacks, ClientConnectedCallbacks clientConnectedCallbacks) {
        this.deviceType = DeviceType.SERVER;
        this.serverPort = port;
        this.serverMessageReceivedCallbacks = serverMessageReceivedCallbacks;
        this.clientConnectedCallbacks = clientConnectedCallbacks;
        new Thread(new ServerReceiver(port, serverMessageReceivedCallbacks, clientConnectedCallbacks)).start();
    }

    public void listenToClient(int port, ServerMessageReceivedCallbacks serverMessageReceivedCallbacks) {
        this.deviceType = DeviceType.SERVER;
        this.serverPort = port;
        this.serverMessageReceivedCallbacks = serverMessageReceivedCallbacks;
        new Thread(new ServerReceiver(port, serverMessageReceivedCallbacks)).start();
    }

    public void sendMessageToServer(String message, ClientMessageSentCallbacks clientMessageSentCallbacks) {
        new Thread(new ClientSender("192.168.0.2", 9999, message, clientMessageSentCallbacks)).start();
    }

}
