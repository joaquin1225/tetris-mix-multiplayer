package server;

import game.GameManager;
import game.GameState;
import game.GameLoop;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class GameServer {
    private final int port;
    private final GameManager gameManager;
    private final GameLoop gameLoop;
    private final long gameLoopTimeLimitMillis;

    public GameServer(int port, GameManager gameManager) {
        this(port, gameManager, 0);
    }

    public GameServer(
            int port,
            GameManager gameManager,
            long timeLimitMillis
    ) {
        this.port = port;
        this.gameManager = gameManager;
        this.gameLoopTimeLimitMillis = timeLimitMillis;
        this.gameLoop = new GameLoop(
                gameManager,
                500,
                timeLimitMillis
        );
    }

    public void start() {
        start(new Scanner(System.in));
    }

    public void start(Scanner console) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println();
            System.out.println("================================");
            System.out.println("     TETRIS MIX MULTIPLAYER");
            System.out.println("================================");
            System.out.println("Tablero: " + gameManager.getBoard().getRows() + " x " + gameManager.getBoard().getColumns());
            System.out.println("Puerto " + port);
            if (gameLoopTimeLimitMillis > 0) {
                System.out.println(
                        "Tiempo limite: "
                                + (gameLoopTimeLimitMillis / 60000)
                                + " minutos"
                );
            }
            System.out.println("================================");
            System.out.println("Esperando jugadores...");
            System.out.println("Escriba START para iniciar la partida.");
            System.out.println();

            Thread adminThread = new Thread(
                    new AdminCommandHandler(console),
                    "tetris-admin-console"
            );
            adminThread.start();

            while(true) {
                Socket client = serverSocket.accept();

                if (gameManager.getGameState() != GameState.WAITING) {
                    System.out.println("Conexion rechazada: la partida ya no acepta jugadores.");
                    client.close();
                } else {
                    System.out.println("Jugador conectado: " + client.getInetAddress().getHostAddress());

                    ClientHandler clientHandler = new ClientHandler(client, gameManager);
                    Thread clientThread = new Thread(clientHandler);
                    clientThread.start();
                }
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }

    private class AdminCommandHandler implements Runnable {
        private final Scanner console;

        private AdminCommandHandler(Scanner console) {
            this.console = console;
        }

        @Override
        public void run() {
            while (gameManager.getGameState() == GameState.WAITING
                    && console.hasNextLine()) {
                String command = console.nextLine().trim();

                if (!"START".equalsIgnoreCase(command)) {
                    System.out.println("Comando no reconocido. Escriba START.");
                    continue;
                }

                if (!gameManager.startGame()) {
                    System.out.println(
                            "La partida no pudo iniciar. "
                                    + "Verifique que exista al menos un jugador "
                                    + "y que todos tengan un spawn valido."
                    );
                    continue;
                }

                if (!gameLoop.start()) {
                    gameManager.finishGame();
                    System.out.println("No se pudo iniciar el GameLoop.");
                    return;
                }

                System.out.println("Partida iniciada.");
                return;
            }
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
        System.out.println("Ingrese limite de tiempo en minutos: ");
        int timeLimitMinutes = sc.nextInt();

        if (rows <= 0 || columns <= 0 || timeLimitMinutes <= 0) {
            System.err.println(
                    "Configuracion invalida. Las dimensiones y el limite "
                            + "de tiempo deben ser mayores a 0."
            );
            sc.close();
            return;
        }

        GameManager gameManager = new GameManager(rows, columns);
        int port = 5000;

        long timeLimitMillis =
                timeLimitMinutes * 60L * 1000L;

        GameServer gameServer =
                new GameServer(port, gameManager, timeLimitMillis);
        sc.nextLine();
        gameServer.start(sc);
    }
}
