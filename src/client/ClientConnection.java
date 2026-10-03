package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientConnection {
    private final String host;
    private final int port;
    private final ClientMessageListener listener;

    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;
    private Thread receiverThread;
    private volatile boolean connected;

    public ClientConnection(
            String host,
            int port,
            ClientMessageListener listener
    ) {
        if (host == null || host.trim().isEmpty()) {
            throw new IllegalArgumentException("El host no puede estar vacio.");
        }
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("El puerto no es valido.");
        }
        if (listener == null) {
            throw new IllegalArgumentException("El listener no puede ser null.");
        }

        this.host = host;
        this.port = port;
        this.listener = listener;
    }

    public synchronized void connect() throws IOException {
        if (connected) {
            return;
        }

        socket = new Socket(host, port);
        input = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream(),
                        StandardCharsets.UTF_8
                )
        );
        output = new PrintWriter(
                socket.getOutputStream(),
                true
        );
        connected = true;

        receiverThread = new Thread(
                new MessageReceiver(),
                "tetris-client-receiver"
        );
        receiverThread.start();
    }

    public synchronized boolean isConnected() {
        return connected;
    }

    public synchronized void sendCommand(String command) {
        if (!connected) {
            throw new IllegalStateException("El cliente no esta conectado.");
        }
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El comando no puede estar vacio."
            );
        }

        output.println(command.trim());
    }

    public void close() {
        Thread threadToJoin;

        synchronized (this) {
            if (!connected && socket == null) {
                return;
            }

            connected = false;
            threadToJoin = receiverThread;
            closeResources();
        }

        if (threadToJoin != null
                && threadToJoin != Thread.currentThread()) {
            try {
                threadToJoin.join(1000);
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private synchronized void closeResources() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException exception) {
            listener.onConnectionError(exception);
        }

        try {
            if (input != null) {
                input.close();
            }
        } catch (IOException exception) {
            listener.onConnectionError(exception);
        }

        if (output != null) {
            output.close();
        }

        input = null;
        output = null;
        socket = null;
        receiverThread = null;
    }

    private class MessageReceiver implements Runnable {
        @Override
        public void run() {
            BufferedReader reader;

            synchronized (ClientConnection.this) {
                reader = input;
            }

            try {
                String message;

                while (connected && (message = reader.readLine()) != null) {
                    listener.onMessage(message);
                }
            } catch (IOException exception) {
                if (connected) {
                    listener.onConnectionError(exception);
                }
            } finally {
                boolean notifyDisconnected;

                synchronized (ClientConnection.this) {
                    notifyDisconnected = connected;
                    connected = false;
                    closeResources();
                }

                if (notifyDisconnected) {
                    listener.onDisconnected();
                }
            }
        }
    }
}
