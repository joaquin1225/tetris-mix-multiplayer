package server;

import game.GameManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class GameServer {
    private final int port;
    private final GameManager gameManager;

    public GameServer(int port, GameManager gameManager) {
        this.port = port;
        this.gameManager = gameManager;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println();
            System.out.println("================================");
            System.out.println("     TETRIS MIX MULTIPLAYER");
            System.out.println("================================");
            System.out.println("Tablero: " + gameManager.getBoard().getRows() + " x " + gameManager.getBoard().getColumns());
            System.out.println("Puerto " + port);
            System.out.println("================================");
            System.out.println("Esperando jugadores...");
            System.out.println();

            while(true) {
                Socket client = serverSocket.accept();
                System.out.println("Jugador conectado: " + client.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(client, gameManager);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("Configuracion del tablero: ");
        System.out.println();

        System.out.println("Ingrese cantidad de filas: ");
        int rows = sc.nextInt();
        System.out.println("Ingrese cantidad de columnas: ");
        int columns = sc.nextInt();

        if (rows <= 0 || columns <= 0) {
            System.err.println("Dimensiones del tablero invalidas. Deben ser mayores a 0.");
            sc.close();
            return;
        }

        GameManager gameManager = new GameManager(rows, columns);
        int port = 5000;

        GameServer gameServer = new GameServer(port, gameManager);
        sc.close();
        gameServer.start();
    }
}
