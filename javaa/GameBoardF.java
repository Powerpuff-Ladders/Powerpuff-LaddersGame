
import java.util.*;

public class GameBoardF {
    final int FINAL_TILE = 70;
    int[] playerPosition;
    Map<Integer, Integer> ladders = new HashMap<>();
    Map<Integer, Integer> snakes = new HashMap<>();
    Random random = new Random();

    VillainF mojoBoss = new VillainF("Mojo Jojo", 200, "Boss");
    boolean mojoAlive = true;

    public GameBoardF(int players) {
        playerPosition = new int[players];
        for (int i = 0; i < players; i++)
            playerPosition[i] = 1;

        ladders.put(4, 20);
        ladders.put(10, 29);
        ladders.put(25, 45);
        ladders.put(39, 55);
        ladders.put(58, 67);

        snakes.put(17, 7);
        snakes.put(30, 19);
        snakes.put(41, 22);
        snakes.put(53, 36);
        snakes.put(64, 48);
    }

    public int rollDice() {
        return random.nextInt(6) + 1;
    }

    public int movePlayer(int player, int roll) {
        int pos = playerPosition[player] + roll;
        if (pos > FINAL_TILE)
            return playerPosition[player];
        if (ladders.containsKey(pos))
            pos = ladders.get(pos);
        else if (snakes.containsKey(pos))
            pos = snakes.get(pos);
        playerPosition[player] = pos;
        return pos;
    }
}
