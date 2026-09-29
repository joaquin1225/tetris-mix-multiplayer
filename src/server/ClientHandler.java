package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            System.out.println("Cliente conectado: " + socket.getInetAddress().getHostAddress());

            sendMessage("Conectando al server de Tetris...");
            String message;

            while((message = in.readLine()) != null) {
                System.out.println("Mensaje recibido de: " + socket.getInetAddress().getHostAddress() + ": " + message);
                sendMessage("Server recibio: " + message);
            }

            System.out.println("Cliente desconectado: " + socket.getInetAddress().getHostAddress());
        } catch (IOException e) {
            System.err.println("Error en la comunicacion con el cliente: " + e.getMessage());
        } finally {
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
