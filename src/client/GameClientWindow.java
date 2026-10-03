package client;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class GameClientWindow extends JFrame {
    private final JTextField hostField;
    private final JTextField portField;
    private final JTextArea messagesArea;
    private final JButton connectButton;
    private final JButton disconnectButton;
    private final JButton leftButton;
    private final JButton rightButton;
    private final JButton rotateLeftButton;
    private final JButton rotateRightButton;
    private final JButton downButton;

    private ClientConnection connection;

    public GameClientWindow() {
        super("Tetris Mix Multiplayer");

        hostField = new JTextField("127.0.0.1", 12);
        portField = new JTextField("5000", 5);
        messagesArea = new JTextArea(12, 45);
        connectButton = new JButton("Conectar");
        disconnectButton = new JButton("Desconectar");
        leftButton = new JButton("Izquierda");
        rightButton = new JButton("Derecha");
        rotateLeftButton = new JButton("G1");
        rotateRightButton = new JButton("G2");
        downButton = new JButton("Abajo");

        configureWindow();
        buildLayout();
        registerActions();
        updateConnectionControls(false);
    }

    private void configureWindow() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));
        setResizable(false);
    }

    private void buildLayout() {
        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectionPanel.setBorder(
                BorderFactory.createTitledBorder("Conexion")
        );
        connectionPanel.add(new JLabel("Host:"));
        connectionPanel.add(hostField);
        connectionPanel.add(new JLabel("Puerto:"));
        connectionPanel.add(portField);
        connectionPanel.add(connectButton);
        connectionPanel.add(disconnectButton);

        messagesArea.setEditable(false);
        messagesArea.setLineWrap(true);
        messagesArea.setWrapStyleWord(true);
        JScrollPane messagesScrollPane = new JScrollPane(messagesArea);
        messagesScrollPane.setBorder(
                BorderFactory.createTitledBorder("Mensajes del servidor")
        );

        JPanel controlsPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        controlsPanel.setBorder(
                BorderFactory.createTitledBorder("Controles")
        );
        controlsPanel.add(new JLabel());
        controlsPanel.add(rotateLeftButton);
        controlsPanel.add(rotateRightButton);
        controlsPanel.add(leftButton);
        controlsPanel.add(downButton);
        controlsPanel.add(rightButton);

        add(connectionPanel, BorderLayout.NORTH);
        add(messagesScrollPane, BorderLayout.CENTER);
        add(controlsPanel, BorderLayout.SOUTH);
        pack();
        setLocationByPlatform(true);
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

        registerKeyboardCommands();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
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

    private void registerKeyboardCommands() {
        getRootPane().getInputMap(
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        ).put(KeyStroke.getKeyStroke("LEFT"), "move-left");
        getRootPane().getActionMap().put(
                "move-left",
                commandAction("LEFT")
        );

        getRootPane().getInputMap(
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        ).put(KeyStroke.getKeyStroke("RIGHT"), "move-right");
        getRootPane().getActionMap().put(
                "move-right",
                commandAction("RIGHT")
        );

        getRootPane().getInputMap(
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        ).put(KeyStroke.getKeyStroke("G"), "rotate-left");
        getRootPane().getActionMap().put(
                "rotate-left",
                commandAction("G1")
        );

        getRootPane().getInputMap(
                javax.swing.JComponent.WHEN_IN_FOCUSED_WINDOW
        ).put(KeyStroke.getKeyStroke("H"), "rotate-right");
        getRootPane().getActionMap().put(
                "rotate-right",
                commandAction("G2")
        );
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
                        appendMessage(message);
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

    private void appendMessage(String message) {
        Runnable appendTask = new Runnable() {
            @Override
            public void run() {
                messagesArea.append(message + System.lineSeparator());
                messagesArea.setCaretPosition(
                        messagesArea.getDocument().getLength()
                );
            }
        };

        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            appendTask.run();
        } else {
            javax.swing.SwingUtilities.invokeLater(appendTask);
        }
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
