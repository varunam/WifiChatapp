package app.wifiduplex.com.wifiduplex.backups;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import app.wifiduplex.com.wifiduplex.R;
import com.lib.serialcommunicator.SocketCommunicator;
import com.lib.serialcommunicator.interfaces.ClientMessageReceivedCallbacks;
import com.lib.serialcommunicator.interfaces.ClientMessageSentCallbacks;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import static app.wifiduplex.com.wifiduplex.backups.JServerActivity.PORT_NUMBER;

/**
 * Created by varun.am on 17/10/18
 */
public class JClientActivity extends AppCompatActivity implements View.OnClickListener, ClientMessageReceivedCallbacks, ClientMessageSentCallbacks {

    private static final String TAG = JClientActivity.class.getSimpleName();

    private EditText ipAddressEditText, sendMessageEditText;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button connectButton, sendMessageButton;
    private TextView chatHistory;

    private Socket socket;
    private String serverIpAddress;

    private OutputStreamWriter outputStreamWriter;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private SocketCommunicator socketCommunicator;

    private boolean chatEnded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        initViews();
        socketCommunicator = new SocketCommunicator();

    }

    private void initViews() {
        ipAddressEditText = findViewById(R.id.server_ip_editText_id);
        sendMessageEditText = findViewById(R.id.client_message_editText_id);
        connectButton = findViewById(R.id.connect_button_id);
        sendMessageButton = findViewById(R.id.client_send_button_id);
        chatHistory = findViewById(R.id.client_chat_history_text_id);
        radioGroup = findViewById(R.id.radioGroupId);

        connectButton.setOnClickListener(this);
        sendMessageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.connect_button_id:
                String ipAddress = ipAddressEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(ipAddress)) {
                    serverIpAddress = ipAddress;
                    Log.e(TAG, "Server Ip Address stored: " + ipAddress);

                    /*Thread clientThread = new Thread(new ClientThread());
                    clientThread.start();*/
                    socketCommunicator.listenToServer(serverIpAddress, PORT_NUMBER, this, null);
                }
                break;
            case R.id.client_send_button_id:
                /*Thread sendMessageThread = new Thread(new SendMessageThread());
                sendMessageThread.start();*/
                int chosenButton = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(chosenButton);
                int port = Integer.parseInt(radioButton.getText().toString());
                socketCommunicator.sendMessageToServer(ipAddressEditText.getText().toString(), port, sendMessageEditText.getText().toString().trim(), this);
                Log.e(TAG, "Sending message to server");
                sendMessageEditText.setText("");
                break;
        }
    }

    @Override
    public void onMessageReceivedByClient(final String messageReceived) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatHistory.append("Server: " + messageReceived + "\n");
            }
        });
    }

    @Override
    public void onMessageReceiveFailure() {

    }

    @Override
    public void clientMessageSentSuccessful(final String messageSent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatHistory.append("Client: " + messageSent + "\n");
            }
        });
    }

    @Override
    public void clientMessageSendFailure() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Message sent failure", Toast.LENGTH_LONG).show();
            }
        });
    }

    /*public class ClientThread extends Thread {

        @Override
        public void run() {
            try {
                socket = new Socket(serverIpAddress, PORT_NUMBER);
                Log.e(TAG, "socket created and connected to server");
            } catch (IOException e) {
                Log.e(TAG, "Couldn't create socket");
                e.printStackTrace();
            }
            while (!chatEnded) {
                try {
                    final String message = receiveMessage();
                    Log.e(TAG, "Received message from server: " + message);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            chatHistory.append("Server: " + message + "\n");
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Couldn't read message");
                    e.printStackTrace();
                }
            }
        }
    }

    public class SendMessageThread extends Thread {

        @Override
        public void run() {
            final String message = sendMessageEditText.getText().toString().trim();
            if (!TextUtils.isEmpty(message)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatHistory.append("Client: " + message + "\n");
                    }
                });
                sendMessage(message);
            }
        }
    }

    public void sendMessage(String message) {
        try {
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
            printWriter = new PrintWriter(outputStreamWriter);
            printWriter.println(message);
            outputStreamWriter.flush();
            Log.e(TAG, "Sent message to server " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String receiveMessage() {
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return bufferedReader.readLine();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            chatEnded = true;
            socket.close();
            bufferedReader.close();
            outputStreamWriter.close();
            printWriter.close();
            Log.e(TAG, "Socket closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}