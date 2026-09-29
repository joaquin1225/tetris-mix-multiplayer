package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class GameServer {
    private final int port;

    public GameServer(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server iniciado en el puerto " + port);
            System.out.println("Esperando jugadores...");

            while(true) {
                Socket client = serverSocket.accept();
                System.out.println("Jugador conectado: " + client.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(client);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = 5000;

        GameServer gameServer = new GameServer(port);
        gameServer.start();
    }
}
