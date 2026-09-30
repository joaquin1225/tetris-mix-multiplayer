package server;

import game.GameManager;
import game.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final GameManager gameManager;
    private BufferedReader in;
    private PrintWriter out;
    private Player player;

    public ClientHandler(Socket socket, GameManager gameManager) {
        this.socket = socket;
        this.gameManager = gameManager;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            player = gameManager.addPlayer();

            System.out.println("Player " + player.getId() + " conectado desde: " + socket.getInetAddress().getHostAddress());

            sendMessage("Conectado al server de Tetris");
            sendMessage("Tu ID de player es: " + player.getId());
            String message;

            while((message = in.readLine()) != null) {
                System.out.println("Mensaje recibido de player " + player.getId() + ": " + message);
                sendMessage("Server recibio: " + message);
            }

            System.out.println("Player " + player.getId() + " desconectado.");
        } catch (IOException e) {
            System.err.println("Error en la comunicacion con el player " + (player != null ? player.getId() : "desconocido") + ": " + e.getMessage());
        } finally {
            if (player != null) {
                gameManager.removePlayer(player.getId());
            }
            closeConnection();
        }
    }

    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

    public void closeConnection() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar la conexion: " + e.getMessage());
        }
    }
}
