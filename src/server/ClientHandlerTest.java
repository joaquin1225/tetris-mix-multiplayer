package server;

import game.GameManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandlerTest {
    public static void main(String[] args) throws Exception {
        testCommands();
        System.out.println("Comandos del cliente: OK");
    }

    private static void testCommands() throws Exception {
        GameManager gameManager = new GameManager(10, 10);

        try (ServerSocket serverSocket = new ServerSocket(0);
             Socket clientSocket = new Socket(
                     "127.0.0.1",
                     serverSocket.getLocalPort()
             );
             Socket serverClientSocket = serverSocket.accept()) {

            ClientHandler handler =
                    new ClientHandler(serverClientSocket, gameManager);
            Thread handlerThread = new Thread(handler);
            handlerThread.start();

            BufferedReader input = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
            );
            PrintWriter output = new PrintWriter(
                    clientSocket.getOutputStream(),
                    true
            );

            input.readLine();
            input.readLine();

            output.println("LEFT");
            assertResponse(input.readLine(), "ACTION_REJECTED LEFT");

            if (!gameManager.startGame()) {
                throw new AssertionError("La partida debía iniciar.");
            }

            output.println("DOWN_PRESSED");
            assertResponse(
                    input.readLine(),
                    "ACTION_OK DOWN_PRESSED"
            );

            output.println("DOWN_RELEASED");
            assertResponse(
                    input.readLine(),
                    "ACTION_OK DOWN_RELEASED"
            );

            output.println("comando_invalido");
            assertResponse(input.readLine(), "ERROR UNKNOWN_COMMAND");

            clientSocket.close();
            handlerThread.join(1000);

            if (handlerThread.isAlive()) {
                throw new AssertionError(
                        "El handler debía terminar al desconectarse."
                );
            }
        }
    }

    private static void assertResponse(
            String actual,
            String expected
    ) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    "Respuesta esperada: " + expected
                            + ", recibida: " + actual
            );
        }
    }
}
