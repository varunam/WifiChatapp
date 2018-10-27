package app.wifiduplex.com.wifiduplex;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.lib.serialcommunicator.SocketCommunicator;
import com.lib.serialcommunicator.interfaces.*;

import java.net.Socket;

/**
 * Created by varun.am on 17/10/18
 */
public class JServerActivity extends AppCompatActivity implements ServerMessageReceivedCallbacks, ClientConnectedCallbacks, SocketsClosedCallbacks, ServerSocketCreationCallbacks, ServerMessageSentCallbacks {

    private static final String TAG = JServerActivity.class.getSimpleName();
    private SocketCommunicator socketCommunicator;
    private Button sendMessageButton;
    private EditText messageEditText;
    private TextView chatHistory, status;
    private Socket clientSocket;

    private int[] ports;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        initViews();

        socketCommunicator = new SocketCommunicator();
        ports = new int[]{9997, 9998, 9999};
        socketCommunicator.listenToClient(ports, this, this, this);
        resetStatus();

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Thread serverThread = new Thread(new SendMessage());
                serverThread.start();*/
                socketCommunicator.sendMessageToClient(clientSocket, messageEditText.getText().toString().trim(), JServerActivity.this);
                messageEditText.setText("");
                Log.e(TAG, "Sending message to client...");
            }
        });

    }

    private void resetStatus() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        status.setText(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));
        status.append("\nPorts: \n" + ports[0] + "," + ports[1] + " & " + ports[2]);
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
    public void onClientConnected(final Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void onClientConnectionFailure(final String failureReason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText("Connection Failure " + failureReason);
            }
        });
    }

    @Override
    public void socketCloseSuccessful() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                status.setText("Sockets closed");
            }
        });
    }

    @Override
    public void socketCloseFailure() {
        status.setText("Sockets close failure");
    }

    @Override
    public void serverSocketCreationSuccessful() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "ServerSocket created", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void serverSocketCreationFailure(final String failureReason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "ServerSocket creation failure " + failureReason, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void serverMessageSendSuccessfull(final String sentMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatHistory.append("Server: " + sentMessage + "\n");
            }
        });
    }

    @Override
    public void serverMessageSendFailure(final String failureReason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(),"Couldn't send message " + failureReason, Toast.LENGTH_LONG).show();
            }
        });
    }
}
