package app.wifiduplex.com.wifiduplex;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.lib.serialcommunicator.SocketCommunicator;
import com.lib.serialcommunicator.interfaces.ClientMessageReceivedCallbacks;
import com.lib.serialcommunicator.interfaces.ClientMessageSentCallbacks;
import com.lib.serialcommunicator.interfaces.SocketsClosedCallbacks;

/**
 * Created by varun.am on 17/10/18
 */
public class JClientActivity extends AppCompatActivity implements View.OnClickListener, ClientMessageReceivedCallbacks, ClientMessageSentCallbacks, SocketsClosedCallbacks {

    private static final String TAG = JClientActivity.class.getSimpleName();

    private EditText ipAddressEditText, sendMessageEditText;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button connectButton, sendMessageButton;
    private TextView chatHistory;

    private String serverIpAddress;

    private SocketCommunicator socketCommunicator;

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
                    socketCommunicator.listenToServer(serverIpAddress, 9997, this);
                }
                break;
            case R.id.client_send_button_id:
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

    @Override
    public void socketCloseSuccessful() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatHistory.append("Socket Closed");
            }
        });
    }

    @Override
    public void socketCloseFailure() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatHistory.append("Couldn't close socket");
            }
        });
    }
}