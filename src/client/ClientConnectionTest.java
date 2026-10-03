package client;

import game.GameManager;
import server.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientConnectionTest {
    public static void main(String[] args) throws Exception {
        testConnectionAndCommands();
        System.out.println("Conexion TCP del cliente: OK");
    }

    private static void testConnectionAndCommands() throws Exception {
        final GameManager gameManager = new GameManager(10, 10);
        final List<String> messages = new ArrayList<>();
        final List<Exception> errors = new ArrayList<>();
        final Object monitor = new Object();

        ClientMessageListener listener = new ClientMessageListener() {
            @Override
            public void onMessage(String message) {
                synchronized (monitor) {
                    messages.add(message);
                    monitor.notifyAll();
                }
            }

            @Override
            public void onDisconnected() {
                synchronized (monitor) {
                    monitor.notifyAll();
                }
            }

            @Override
            public void onConnectionError(Exception exception) {
                synchronized (monitor) {
                    errors.add(exception);
                    monitor.notifyAll();
                }
            }
        };

        ServerSocket serverSocket = new ServerSocket(0);
        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket serverSideSocket = serverSocket.accept();
                    new ClientHandler(
                            serverSideSocket,
                            gameManager
                    ).run();
                } catch (IOException exception) {
                    synchronized (monitor) {
                        errors.add(exception);
                        monitor.notifyAll();
                    }
                }
            }
        });

        serverThread.start();

        ClientConnection connection = new ClientConnection(
                "127.0.0.1",
                serverSocket.getLocalPort(),
                listener
        );

        try {
            connection.connect();

            waitForMessages(monitor, messages, 3);
            assertNoErrors(errors);
            waitForStateMessage(messages);

            connection.sendCommand("LEFT");
            waitForMessage(monitor, messages, "ACTION_REJECTED LEFT");

            connection.sendCommand("UNKNOWN");
            waitForMessage(monitor, messages, "ERROR UNKNOWN_COMMAND");
        } finally {
            connection.close();
            serverSocket.close();
            serverThread.join(1000);
        }

        if (connection.isConnected()) {
            throw new AssertionError(
                    "La conexion debia estar cerrada."
            );
        }
        if (serverThread.isAlive()) {
            throw new AssertionError(
                    "El handler del servidor debia terminar."
            );
        }
        assertNoErrors(errors);
    }

    private static void waitForMessages(
            Object monitor,
            List<String> messages,
            int expectedCount
    ) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 2000;

        synchronized (monitor) {
            while (messages.size() < expectedCount
                    && System.currentTimeMillis() < deadline) {
                monitor.wait(50);
            }
        }

        if (messages.size() < expectedCount) {
            throw new AssertionError(
                    "No se recibieron los mensajes iniciales."
            );
        }
    }

    private static void waitForMessage(
            Object monitor,
            List<String> messages,
            String expectedMessage
    ) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 2000;

        synchronized (monitor) {
            while (!messages.contains(expectedMessage)
                    && System.currentTimeMillis() < deadline) {
                monitor.wait(50);
            }
        }

        if (!messages.contains(expectedMessage)) {
            throw new AssertionError(
                    "No se recibio: " + expectedMessage
            );
        }
    }

    private static void assertNoErrors(List<Exception> errors) {
        if (!errors.isEmpty()) {
            throw new AssertionError(
                    "Se recibieron errores de conexion: " + errors
            );
        }
    }

    private static void waitForStateMessage(List<String> messages) {
        for (String message : messages) {
            if (message.startsWith("STATE|")) {
                GameStateMessage.parse(message);
                return;
            }
        }

        throw new AssertionError("No se recibio el estado inicial.");
    }
}
