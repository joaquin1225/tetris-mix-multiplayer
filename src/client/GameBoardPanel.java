package client;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

public class GameBoardPanel extends JPanel {
    private static final int DEFAULT_CELL_SIZE = 24;
    private static final int BORDER_SIZE = 2;

    private List<String> rows;
    private int cellSize = DEFAULT_CELL_SIZE;

    public GameBoardPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(600, 500));
    }

    public synchronized void updateState(GameStateMessage state) {
        rows = state.getBoardRows();
        setPreferredSize(
                new Dimension(
                        state.getColumns() * cellSize + BORDER_SIZE,
                        state.getRows() * cellSize + BORDER_SIZE
                )
        );
        revalidate();
        repaint();
    }

    public synchronized void setCellSize(int cellSize) {
        if (cellSize < 10) {
            throw new IllegalArgumentException(
                    "El tamano de celda debe ser al menos 10."
            );
        }
        this.cellSize = cellSize;
    }

    @Override
    protected synchronized void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (rows == null || rows.isEmpty()) {
            return;
        }

        graphics.setFont(
                new Font(Font.MONOSPACED, Font.BOLD, Math.max(9, cellSize - 7))
        );

        for (int row = 0; row < rows.size(); row++) {
            String boardRow = rows.get(row);

            for (int column = 0; column < boardRow.length(); column++) {
                int x = column * cellSize;
                int y = row * cellSize;
                char value = boardRow.charAt(column);

                graphics.setColor(colorFor(value));
                graphics.fillRect(x, y, cellSize, cellSize);
                graphics.setColor(Color.LIGHT_GRAY);
                graphics.drawRect(x, y, cellSize, cellSize);

                if (value != '.') {
                    graphics.setColor(Color.BLACK);
                    String cellValue = String.valueOf(value);
                    graphics.drawString(
                            cellValue,
                            x + Math.max(3, cellSize / 3),
                            y + cellSize - Math.max(4, cellSize / 3)
                    );
                }
            }
        }
    }

    private Color colorFor(char value) {
        if (value == '.') {
            return Color.WHITE;
        }

        int playerIndex = Character.toLowerCase(value) - 'a';
        Color[] colors = {
                new Color(180, 220, 255),
                new Color(255, 210, 170),
                new Color(190, 235, 190),
                new Color(235, 200, 245),
                new Color(255, 235, 150)
        };

        return colors[Math.max(0, playerIndex) % colors.length];
    }
}
