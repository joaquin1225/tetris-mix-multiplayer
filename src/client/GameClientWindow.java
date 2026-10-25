package client;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.KeyEventDispatcher;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class GameClientWindow extends JFrame {
    private final JTextField hostField;
    private final JTextField portField;
    private final JLabel messageLabel;
    private final JTextArea scoresArea;
    private final JLabel gameStateLabel;
    private final JLabel dimensionsLabel;
    private final GameBoardPanel boardPanel;
    private final JButton connectButton;
    private final JButton disconnectButton;
    private final JButton leftButton;
    private final JButton rightButton;
    private final JButton rotateLeftButton;
    private final JButton rotateRightButton;
    private final JButton downButton;
    private final KeyEventDispatcher keyEventDispatcher;

    private ClientConnection connection;
    private boolean downKeyPressed;
    private int configuredRows;
    private int configuredColumns;
    private boolean initialSizeApplied;

    public GameClientWindow() {
        super("Tetris Mix Multiplayer");

        hostField = new JTextField("127.0.0.1", 12);
        portField = new JTextField("5000", 5);
        messageLabel = new JLabel("Sin conexion");
        scoresArea = new JTextArea(4, 16);
        gameStateLabel = new JLabel("Estado: desconectado");
        dimensionsLabel = new JLabel("Tablero: - x -");
        boardPanel = new GameBoardPanel();
        connectButton = new JButton("Conectar");
        disconnectButton = new JButton("Desconectar");
        leftButton = new JButton("<");
        rightButton = new JButton(">");
        rotateLeftButton = new JButton("G1");
        rotateRightButton = new JButton("G2");
        downButton = new JButton("v");
        keyEventDispatcher = createKeyEventDispatcher();

        configureWindow();
        buildLayout();
        registerActions();
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(keyEventDispatcher);
        updateConnectionControls(false);
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        setMinimumSize(new Dimension(700, 450));
    }

    private void buildLayout() {
        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectionPanel.setBorder(
                BorderFactory.createTitledBorder("Conexion")
        );
        connectionPanel.add(new JLabel("IP:"));
        connectionPanel.add(hostField);
        connectionPanel.add(new JLabel("Fil:"));
        connectionPanel.add(new JLabel("-"));
        connectionPanel.add(new JLabel("Col:"));
        connectionPanel.add(new JLabel("-"));
        connectionPanel.add(new JLabel("Puerto:"));
        connectionPanel.add(portField);
        connectionPanel.add(connectButton);
        connectionPanel.add(disconnectButton);

        messageLabel.setBorder(
                BorderFactory.createEmptyBorder(3, 6, 3, 6)
        );

        JPanel controlsPanel = new JPanel(new GridLayout(3, 3, 4, 4));
        controlsPanel.setBorder(
                BorderFactory.createTitledBorder("Controles")
        );
        controlsPanel.add(new JLabel());
        controlsPanel.add(rotateLeftButton);
        controlsPanel.add(new JLabel());
        controlsPanel.add(leftButton);
        controlsPanel.add(downButton);
        controlsPanel.add(rightButton);
        controlsPanel.add(new JLabel());
        controlsPanel.add(rotateRightButton);
        controlsPanel.add(new JLabel());

        for (java.awt.Component component : controlsPanel.getComponents()) {
            if (component instanceof JButton) {
                ((JButton) component).setMargin(new Insets(4, 8, 4, 8));
                ((JButton) component).setFocusable(false);
            }
        }

        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setBorder(
                BorderFactory.createTitledBorder("Tablero compartido")
        );
        boardContainer.add(boardPanel, BorderLayout.CENTER);

        scoresArea.setEditable(false);
        scoresArea.setBorder(
                BorderFactory.createTitledBorder("Jugadores y puntos")
        );

        JPanel statusPanel = new JPanel(new GridLayout(2, 1));
        statusPanel.add(gameStateLabel);
        statusPanel.add(dimensionsLabel);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.add(statusPanel, BorderLayout.NORTH);
        rightPanel.add(scoresArea, BorderLayout.CENTER);
        rightPanel.add(controlsPanel, BorderLayout.SOUTH);
        rightPanel.setPreferredSize(new Dimension(180, 270));

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.add(boardContainer, BorderLayout.CENTER);
        centerPanel.add(messageLabel, BorderLayout.SOUTH);

        add(connectionPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
        setSize(new Dimension(820, 520));
    }

    private void registerActions() {
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                connect();
            }
        });

        disconnectButton.addActionListener(
                new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        disconnect();
                    }
                }
        );

        leftButton.addActionListener(commandAction("LEFT"));
        rightButton.addActionListener(commandAction("RIGHT"));
        rotateLeftButton.addActionListener(commandAction("G1"));
        rotateRightButton.addActionListener(commandAction("G2"));

        downButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent event) {
                sendCommand("DOWN_PRESSED");
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent event) {
                sendCommand("DOWN_RELEASED");
            }
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
                KeyboardFocusManager.getCurrentKeyboardFocusManager()
                        .removeKeyEventDispatcher(keyEventDispatcher);
                disconnect();
            }
        });
    }

    private javax.swing.Action commandAction(
            final String command
    ) {
        return new javax.swing.AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent event) {
                sendCommand(command);
            }
        };
    }

    private KeyEventDispatcher createKeyEventDispatcher() {
        return new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {
                if (!isActive()
                        || connection == null
                        || !connection.isConnected()) {
                    return false;
                }

                if (event.getID() == KeyEvent.KEY_PRESSED) {
                    if (event.getKeyCode() == KeyEvent.VK_LEFT) {
                        sendCommand("LEFT");
                        event.consume();
                        return true;
                    }
                    if (event.getKeyCode() == KeyEvent.VK_RIGHT) {
                        sendCommand("RIGHT");
                        event.consume();
                        return true;
                    }
                    if (event.getKeyCode() == KeyEvent.VK_DOWN) {
                        if (!downKeyPressed) {
                            downKeyPressed = true;
                            sendCommand("DOWN_PRESSED");
                        }
                        event.consume();
                        return true;
                    }
                }

                if (event.getID() == KeyEvent.KEY_RELEASED
                        && event.getKeyCode() == KeyEvent.VK_DOWN) {
                    if (downKeyPressed) {
                        downKeyPressed = false;
                        sendCommand("DOWN_RELEASED");
                    }
                    event.consume();
                    return true;
                }

                return false;
            }
        };
    }

    private void connect() {
        if (connection != null && connection.isConnected()) {
            return;
        }

        final int port;

        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException exception) {
            appendMessage("Puerto invalido.");
            return;
        }

        connection = new ClientConnection(
                hostField.getText().trim(),
                port,
                new ClientMessageListener() {
                    @Override
                    public void onMessage(String message) {
                        processServerMessage(message);
                    }

                    @Override
                    public void onDisconnected() {
                        updateConnectionControls(false);
                        appendMessage("Desconectado del servidor.");
                    }

                    @Override
                    public void onConnectionError(Exception exception) {
                        appendMessage(
                                "Error de conexion: "
                                        + exception.getMessage()
                        );
                    }
                }
        );

        try {
            connection.connect();
            updateConnectionControls(true);
            appendMessage("Conectando...");
        } catch (IOException exception) {
            appendMessage("No se pudo conectar: " + exception.getMessage());
            connection.close();
            connection = null;
        } catch (IllegalArgumentException exception) {
            appendMessage("Configuracion invalida: " + exception.getMessage());
            connection = null;
        }
    }

    private void disconnect() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
        updateConnectionControls(false);
    }

    private void sendCommand(String command) {
        if (connection == null || !connection.isConnected()) {
            appendMessage("No hay una conexion activa.");
            return;
        }

        try {
            connection.sendCommand(command);
        } catch (IllegalStateException exception) {
            appendMessage(exception.getMessage());
        }
    }

    private void updateConnectionControls(boolean connected) {
        connectButton.setEnabled(!connected);
        disconnectButton.setEnabled(connected);
        hostField.setEnabled(!connected);
        portField.setEnabled(!connected);
        leftButton.setEnabled(connected);
        rightButton.setEnabled(connected);
        rotateLeftButton.setEnabled(connected);
        rotateRightButton.setEnabled(connected);
        downButton.setEnabled(connected);
    }

    private void appendMessage(final String message) {
        Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                messageLabel.setText(message);
            }
        };

        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            updateTask.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(updateTask);
        }
    }

    private void processServerMessage(String message) {
        if (message.startsWith("STATE|")) {
            try {
                final GameStateMessage state =
                        GameStateMessage.parse(message);
                javax.swing.SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        gameStateLabel.setText(
                                "Estado: " + state.getState()
                        );
                        updateConnectionDimensions(state);
                        updateScores(state);
                    }
                });
            } catch (IllegalArgumentException exception) {
                appendMessage("Estado invalido: " + exception.getMessage());
            }
            return;
        }

        appendMessage(message);
    }

    private void updateScores(GameStateMessage state) {
        StringBuilder text = new StringBuilder();

        for (GameStateMessage.PlayerState player : state.getPlayers()) {
            text.append("Jugador ")
                    .append(player.getId())
                    .append(": ")
                    .append(player.getScore())
                    .append(System.lineSeparator());
        }

        scoresArea.setText(text.toString());
    }

        private void updateConnectionDimensions(GameStateMessage state) {
        dimensionsLabel.setText(
                "Tablero: " + state.getRows() + " x " + state.getColumns()
        );

        if (configuredRows != state.getRows()
                || configuredColumns != state.getColumns()) {

            configuredRows = state.getRows();
            configuredColumns = state.getColumns();

            Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
            int maxBoardWidth = (int) (screen.width * 0.65);
            int maxBoardHeight = (int) (screen.height * 0.72);
            int cellSize = Math.min(
                    maxBoardWidth / Math.max(1, state.getColumns()),
                    maxBoardHeight / Math.max(1, state.getRows())
            );
            cellSize = Math.max(10, Math.min(28, cellSize));
            boardPanel.setCellSize(cellSize);

            Dimension contentSize = getContentPane().getPreferredSize();
            int windowWidth = contentSize.width
                    + getInsets().left
                    + getInsets().right;
            int windowHeight = contentSize.height
                    + getInsets().top
                    + getInsets().bottom;
            int maxWindowWidth = screen.width - 40;
            int maxWindowHeight = screen.height - 80;

            if (!initialSizeApplied) {
                setSize(
                        Math.max(700, Math.min(windowWidth, maxWindowWidth)),
                        Math.max(450, Math.min(windowHeight, maxWindowHeight))
                );
                setLocationRelativeTo(null);
                initialSizeApplied = true;
            }
        }

        boardPanel.updateState(state);
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameClientWindow().setVisible(true);
            }
        });
    }
}
