package client;

public class GameStateMessageTest {
    public static void main(String[] args) {
        String message =
                "STATE|RUNNING|2|3|a..,.b.|a,10,T,0,0,1;b,25,L,1,1,0";
        GameStateMessage state = GameStateMessage.parse(message);

        if (!"RUNNING".equals(state.getState())
                || state.getRows() != 2
                || state.getColumns() != 3
                || state.getBoardRows().size() != 2
                || state.getPlayers().size() != 2
                || state.getPlayers().get(0).getScore() != 10) {
            throw new AssertionError("El estado no se parseo correctamente.");
        }

        System.out.println("Parser de estado del juego: OK");
    }
}
