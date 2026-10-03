package server;

import game.GameManager;
import game.GameState;
import game.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Locale;

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
                sendMessage(handleCommand(message));
            }

            System.out.println("Player " + player.getId() + " desconectado.");
        } catch (IOException e) {
            System.err.println("Error en la comunicacion con el player " + (player != null ? player.getId() : "desconocido") + ": " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("Conexion rechazada: " + e.getMessage());
        } finally {
            if (player != null) {
                player.releaseFastDrop();
            }
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

    String handleCommand(String message) {
        if (message == null) {
            return "ERROR UNKNOWN_COMMAND";
        }

        String command = message.trim().toUpperCase(Locale.ROOT);

        if (!isKnownCommand(command)) {
            return "ERROR UNKNOWN_COMMAND";
        }

        if (gameManager.getGameState() != GameState.RUNNING) {
            return "ACTION_REJECTED " + command;
        }

        boolean accepted;

        switch (command) {
            case "LEFT":
                accepted = gameManager.moveLeft(player);
                break;
            case "RIGHT":
                accepted = gameManager.moveRight(player);
                break;
            case "G1":
                accepted = gameManager.rotateCounterClockwise(player);
                break;
            case "G2":
                accepted = gameManager.rotateClockwise(player);
                break;
            case "DOWN_PRESSED":
                accepted = player.pressFastDrop();
                break;
            case "DOWN_RELEASED":
                player.releaseFastDrop();
                accepted = true;
                break;
            default:
                return "ERROR UNKNOWN_COMMAND";
        }

        return (accepted ? "ACTION_OK " : "ACTION_REJECTED ") + command;
    }

    private boolean isKnownCommand(String command) {
        return "LEFT".equals(command)
                || "RIGHT".equals(command)
                || "G1".equals(command)
                || "G2".equals(command)
                || "DOWN_PRESSED".equals(command)
                || "DOWN_RELEASED".equals(command);
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
