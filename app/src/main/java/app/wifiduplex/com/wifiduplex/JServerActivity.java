package app.wifiduplex.com.wifiduplex;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import app.wifiduplex.com.serialcommunicator.SocketCommunicator;
import app.wifiduplex.com.serialcommunicator.interfaces.ClientConnectedCallbacks;
import app.wifiduplex.com.serialcommunicator.interfaces.ServerMessageReceivedCallbacks;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by varun.am on 17/10/18
 */
public class JServerActivity extends AppCompatActivity implements ServerMessageReceivedCallbacks, ClientConnectedCallbacks {

    private static final String TAG = JServerActivity.class.getSimpleName();
    public static final int PORT_NUMBER = 9999;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private Button sendMessageButton;
    private EditText messageEditText;
    private TextView chatHistory, status;

    private OutputStreamWriter outputStreamWriter;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    private boolean chatEnded = false;
    private int[] ports;

    private Thread serverThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        initViews();

        /*sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread serverThread = new Thread(new SendMessage());
                serverThread.start();
                messageEditText.setText("");
                Log.e(TAG, "Sending message to client...");
            }
        });*/

        //serverThread = new Thread(new MessagesListener());
        SocketCommunicator socketCommunicator = new SocketCommunicator();
        ports = new int[]{9998, 9999};
        socketCommunicator.listenToClient(ports, this, this);
        resetStatus();

    }

    private void resetStatus() {

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        status.setText(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
        status.append("\nPorts: " + ports[0] + " & " + ports[1]);
        //serverThread.start();
    }

    private void initViews() {
        sendMessageButton = findViewById(R.id.send_message_button_id);
        messageEditText = findViewById(R.id.message_edit_text_id);
        chatHistory = findViewById(R.id.history_text_id);
        status = findViewById(R.id.status_text_id);
    }

    @Override
    public void onMessageReceivedByServer(final String messageReceived) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatHistory.append("Client: " + messageReceived + "\n");
            }
        });
    }

    @Override
    public void onClientConnected(String clientIpAddress, int clientPort) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText("Connected");
            }
        });
    }

    @Override
    public void onClientConnectionFailure() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText("Connection Failure");
            }
        });
    }

/*    public class SendMessage extends Thread {

        @Override
        public void run() {
            final String message = messageEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(message)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatHistory.append("Server: " + message + "\n");
                    }
                });
                sendMessage(message);
            }
        }
    }

    public class MessagesListener extends Thread {

        public MessagesListener() {
            try {
                serverSocket = new ServerSocket(PORT_NUMBER);
                Log.e(TAG, "serverSocket created");
            } catch (Exception e) {
                Log.e(TAG, "Couldn't create serverSocket");
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                Log.e(TAG, "Waiting for client...");
                clientSocket = serverSocket.accept();
                Log.e(TAG, "Client connected with ip: " + clientSocket.getInetAddress() + " at port: " + clientSocket.getPort());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        status.setText("Connected");
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Couldn't accept socket connection");
            }

            while (!chatEnded) {
                try {
                    Log.e(TAG,"Waiting for message...");
                    final String message = receiveMessage();
                    Log.e(TAG,"Received message: " + message);
                    if (clientSocket!=null && clientSocket.isConnected()) {
                        clientSocket.close();
                        chatEnded = true;
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatHistory.append("Client: " + message + "\n");
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "'Couldn't receive message");
                    e.printStackTrace();
                }
            }

            Log.e(TAG, "MessageListener stopped");
            chatEnded = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resetStatus();
                }
            });
        }
    }

     public void sendMessage(String message) {
        try {
            outputStreamWriter = new OutputStreamWriter(clientSocket.getOutputStream());
            printWriter = new PrintWriter(outputStreamWriter);
            printWriter.println(message);
            outputStreamWriter.flush();
            System.out.println("Sent data to server: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String receiveMessage() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            return bufferedReader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        status.setText("Socket Closed");
        try {
            chatEnded = true;
            serverSocket.close();
            clientSocket.close();
            bufferedReader.close();
            outputStreamWriter.close();
            printWriter.close();
            Log.e(TAG, "Sockets closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
