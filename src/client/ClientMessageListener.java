package client;

public interface ClientMessageListener {
    void onMessage(String message);

    void onDisconnected();

    void onConnectionError(Exception exception);
}
