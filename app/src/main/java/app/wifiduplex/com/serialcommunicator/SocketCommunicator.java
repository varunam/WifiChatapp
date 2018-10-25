package app.wifiduplex.com.serialcommunicator;

import app.wifiduplex.com.serialcommunicator.interfaces.ClientConnectedCallbacks;
import app.wifiduplex.com.serialcommunicator.interfaces.ClientMessageReceivedCallbacks;
import app.wifiduplex.com.serialcommunicator.interfaces.ClientMessageSentCallbacks;
import app.wifiduplex.com.serialcommunicator.interfaces.ServerMessageReceivedCallbacks;

/**
 * Created by varun.am on 25/10/18
 */
public class SocketCommunicator {


    public void sendMessageToServer(String ipAddress, int port, String message, ClientMessageSentCallbacks clientMessageSentCallbacks) {
        new Thread(new ClientSender(ipAddress, port, message, clientMessageSentCallbacks)).start();
    }

    public void listenToServer(String serverIpAddress, int port, ClientMessageReceivedCallbacks clientMessageReceivedCallbacks) {
        new Thread(new ClientReceiver(serverIpAddress, port, clientMessageReceivedCallbacks)).start();
    }

    public void listenToClient(int listeningPort, ServerMessageReceivedCallbacks serverMessageReceivedCallbacks, ClientConnectedCallbacks clientConnectedCallbacks){
        new Thread(new ServerReceiver(listeningPort, serverMessageReceivedCallbacks, clientConnectedCallbacks)).start();
    }

}
