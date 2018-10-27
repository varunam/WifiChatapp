package com.lib.serialcommunicator;

import com.lib.serialcommunicator.interfaces.ServerMessageSentCallbacks;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by varun.am on 27/10/18
 */
public class ServerSender extends Thread {

    private static final String TAG = ServerSender.class.getSimpleName();
    private Logger Log = Logger.getLogger(TAG);

    private Socket clientSocket;
    private String message;
    private ServerMessageSentCallbacks serverMessageSentCallbacks;

    public ServerSender(Socket clientSocket, String messageToSend, ServerMessageSentCallbacks serverMessageSentCallbacks) {
        this.clientSocket = clientSocket;
        this.message = messageToSend;
        this.serverMessageSentCallbacks = serverMessageSentCallbacks;
    }

    @Override
    public void run() {
        sendMessage(message);
    }

    public void sendMessage(String message) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream());
            PrintWriter printWriter = new PrintWriter(outputStreamWriter);
            printWriter.println(message);
            outputStreamWriter.flush();
            if (serverMessageSentCallbacks != null)
                serverMessageSentCallbacks.serverMessageSendSuccessfull(message);
            outputStreamWriter.close();
            printWriter.close();
            Log.log(Level.INFO, "Sent message to client " + message);
        } catch (Exception e) {
            if (serverMessageSentCallbacks != null)
                serverMessageSentCallbacks.serverMessageSendFailure(e.getMessage());
            Log.log(Level.SEVERE, "Couldn't send message to client " + message);
            e.printStackTrace();
        }
    }
}
