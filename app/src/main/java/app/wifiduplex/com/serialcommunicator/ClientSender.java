package app.wifiduplex.com.serialcommunicator;

import app.wifiduplex.com.serialcommunicator.interfaces.ClientMessageSentCallbacks;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by varun.am on 24/10/18
 */
public class ClientSender extends Thread {

    private static final String TAG = ClientSender.class.getSimpleName();
    private Logger Log = Logger.getLogger(TAG);
    private String message;

    private OutputStreamWriter outputStreamWriter;
    private PrintWriter printWriter;
    private String serverIpAddress;
    private int port;
    private Socket socket;
    private ClientMessageSentCallbacks clientMessageSentCallbacks;

    public ClientSender(String serverIpAddress, int port, String message, ClientMessageSentCallbacks clientMessageSentCallbacks) {
        this.serverIpAddress = serverIpAddress;
        this.port = port;
        this.clientMessageSentCallbacks = clientMessageSentCallbacks;
        this.message = message;
    }

    @Override
    public void run() {
        sendMessage(message);
    }

    public void sendMessage(String message) {
        try {
            socket = new Socket(serverIpAddress, port);
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            printWriter = new PrintWriter(outputStreamWriter);
            printWriter.println(message);
            outputStreamWriter.flush();
            if (clientMessageSentCallbacks != null)
                clientMessageSentCallbacks.clientMessageSentSuccessful(message);
            outputStreamWriter.close();
            socket.close();
            Log.log(Level.INFO, "Sent message to server " + message);
        } catch (Exception e) {
            if (clientMessageSentCallbacks != null)
                clientMessageSentCallbacks.clientMessageSendFailure();
            e.printStackTrace();
        }
    }
}
