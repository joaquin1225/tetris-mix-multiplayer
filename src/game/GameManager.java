package game;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private final List<Player> players;
    private final Board board;

    public GameManager(int rows, int columns) {
        players = new ArrayList<>();
        board = new Board(rows, columns);
    }

    public synchronized Player addPlayer() {
        String playerId = generatePlayerId();
        Player player = new Player(playerId);
        players.add(player);
        System.out.println("Player " + player.getId() + " agregado. Total de jugadores: " + players.size());
        return player;
    }

    public synchronized void removePlayer(String playerId) {
        Player player = findPlayerById(playerId);
        if (player != null) {
            players.remove(player);
            System.out.println("Player " + playerId + " eliminado. Total de jugadores: " + players.size());
        }
    }

    public synchronized Player findPlayerById(String playerId) {
        for (Player player : players) {
            if (player.getId().equals(playerId)) {
                return player;
            }
        }
        return null;
    }

    private synchronized String generatePlayerId() {
        char id = 'a';
        while(findPlayerById(String.valueOf(id)) != null) {
            id++;
        }
        return String.valueOf(id);
    }

    public synchronized int getPlayerCount() {
        return players.size();
    }

    public Board getBoard() {
        return board;
    }
}
