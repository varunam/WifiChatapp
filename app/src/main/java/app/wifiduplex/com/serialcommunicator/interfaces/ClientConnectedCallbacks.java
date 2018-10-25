package app.wifiduplex.com.serialcommunicator.interfaces;

/**
 * Created by varun.am on 24/10/18
 */
public interface ClientConnectedCallbacks {
    public void onClientConnected(String clientIpAddress, int clientPort);
    public void onClientConnectionFailure();
}
