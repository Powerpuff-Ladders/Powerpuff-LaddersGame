import javax.swing.*;
import java.awt.*;
import java.util.*;

/**
 * 🌈 Powerpuff Ladders - With Ladder Images 🧠
 * 
 * - 3 players (Blossom, Bubbles, Buttercup)
 * - 70-tile board (7x10)
 * - Ladders shown with images (heal +10 HP)
 * - Snakes spawn villains (Him, Fuzzy Lumpkins)
 * - Mojo Jojo boss on final tile (200 HP)
 * - GUI game board, HP bars, logs, restart
 */

public class PowerpuffLadders {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PowerpuffGirlF[] players = new PowerpuffGirlF[] {
                    new PowerpuffGirlF("Blossom", 100, new String[] { "Petal Punch", "Rocket Beam", "Flower Fury" }),
                    new PowerpuffGirlF("Bubbles", 100, new String[] { "Bubble Blast", "Sonic Cry", "Hyper Hug" }),
                    new PowerpuffGirlF("Buttercup", 100, new String[] { "Power Kick", "Tornado Spin", "Mega Smash" })
            };
            GameBoardF board = new GameBoardF(players.length);
            new GameFrameF(board, players);
        });
    }
}

/* ======================= MODEL CLASSES ======================= */

class CharacterF {
    String name;
    int health;

    public CharacterF(String name, int health) {
        this.name = name;
        this.health = health;
    }

    public void takeDamage(int d) {
        health -= d;
        if (health < 0)
            health = 0;
    }

    public boolean isAlive() {
        return health > 0;
    }
}

class PowerpuffGirlF extends CharacterF {
    String[] skills;

    public PowerpuffGirlF(String name, int health, String[] skills) {
        super(name, health);
        this.skills = skills;
    }

    public int getSkillDamage(int choice) {
        if (choice == 1)
            return 15;
        if (choice == 2)
            return 25;
        if (choice == 3)
            return 35;
        return 0;
    }
}

class VillainF extends CharacterF {
    String type;

    public VillainF(String name, int health, String type) {
        super(name, health);
        this.type = type;
    }
}

class GameBoardF {
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

/* ======================= GUI + GAME LOGIC ======================= */

class GameFrameF extends JFrame {
    GameBoardF board;
    PowerpuffGirlF[] players;
    BoardPanelF boardPanel;
    ControlPanelF controlPanel;

    public GameFrameF(GameBoardF board, PowerpuffGirlF[] players) {
        this.board = board;
        this.players = players;

        setTitle("🌈 Powerpuff Ladders - With Ladder Images 🧠");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        boardPanel = new BoardPanelF(board, players);
        controlPanel = new ControlPanelF(board, players, boardPanel);

        add(boardPanel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.EAST);
        setVisible(true);
    }
}

/* --- Board Drawing --- */

class BoardPanelF extends JPanel {
    GameBoardF board;
    PowerpuffGirlF[] players;
    Image ladderImg;

    public BoardPanelF(GameBoardF board, PowerpuffGirlF[] players) {
        this.board = board;
        this.players = players;
        setPreferredSize(new Dimension(680, 680));
        setBackground(Color.WHITE);

        // Load ladder image
        ladderImg = new ImageIcon("ladder1-removebg-preview.png").getImage();
    }

    public void refresh() {
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int cols = 10, rows = 7, w = getWidth(), h = getHeight(), cellW = w / cols,
                cellH = h / rows;

        // Grid
        g.setColor(Color.LIGHT_GRAY);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = c * cellW, y = r * cellH;
                g.drawRect(x, y, cellW, cellH);
                g.setFont(new Font("Arial", Font.PLAIN, 10));
                g.drawString(String.valueOf(r * cols + c + 1), x + 4, y + 12);
            }
        }

        // Draw ladders as images
        for (Map.Entry<Integer, Integer> e : board.ladders.entrySet()) {
            Point from = tileToPoint(e.getKey(), rows, cols, w, h);
            Point to = tileToPoint(e.getValue(), rows, cols, w, h);
            int x = Math.min(from.x, to.x) - 25;
            int y = Math.min(from.y, to.y) - 25;
            int width = Math.abs(from.x - to.x) + 50;
            int height = Math.abs(from.y - to.y) + 50;
            g.drawImage(ladderImg, x, y, width, height, this);
        }

        // Draw snakes (still lines)
        g.setColor(Color.RED);
        for (Map.Entry<Integer, Integer> e : board.snakes.entrySet()) {
            Point from = tileToPoint(e.getKey(), rows, cols, w, h);
            Point to = tileToPoint(e.getValue(), rows, cols, w, h);
            g.drawLine(from.x, from.y, to.x, to.y);
            g.drawString("V", (from.x + to.x) / 2, (from.y + to.y) / 2);
        }

        // Mojo Jojo
        if (board.mojoAlive) {
            Point bossP = tileToPoint(board.FINAL_TILE, rows, cols, w, h);
            g.setColor(Color.MAGENTA);
            g.fillOval(bossP.x - 15, bossP.y - 15, 30, 30);
            g.setColor(Color.WHITE);
            g.drawString("M", bossP.x - 4, bossP.y + 5);
            g.setColor(Color.BLACK);
            g.drawString("Mojo Jojo", bossP.x - 25, bossP.y - 20);
        }

        // Players
        Color[] colors = { Color.PINK, Color.CYAN, Color.GREEN };
        for (int i = 0; i < players.length; i++) {
            int tile = board.playerPosition[i];
            Point p = tileToPoint(tile, rows, cols, w, h);
            g.setColor(colors[i]);
            g.fillOval(p.x - 10 + (i * 8), p.y - 10, 20, 20);
            g.setColor(Color.BLACK);
            g.drawString(players[i].name.substring(0, 1), p.x - 4 + (i * 8), p.y + 4);
        }
    }

    private Point tileToPoint(int tile, int rows, int cols, int w, int h) {
        int index = Math.max(1, Math.min(board.FINAL_TILE, tile)) - 1;
        int r = index / cols;
        int c = index % cols;
        int cellW = w / cols;
        int cellH = h / rows;
        return new Point(c * cellW + cellW / 2, r * cellH + cellH / 2);
    }
}

/* --- Controls and Battle System --- */
class ControlPanelF extends JPanel {
    GameBoardF board;
    PowerpuffGirlF[] players;
    BoardPanelF boardPanel;
    JTextArea log;
    JButton rollButton;
    JButton[] skillButtons = new JButton[3];
    JLabel[] hpLabels;
    int currentPlayer = 0;
    VillainF activeVillain = null;
    Random rng = new Random();

    public ControlPanelF(GameBoardF board, PowerpuffGirlF[] players, BoardPanelF boardPanel) {
        this.board = board;
        this.players = players;
        this.boardPanel = boardPanel;

        setLayout(new BorderLayout(6, 6));
        setPreferredSize(new Dimension(300, 680));

        JPanel info = new JPanel(new GridLayout(players.length, 1, 5, 5));
        hpLabels = new JLabel[players.length];
        for (int i = 0; i < players.length; i++) {
            hpLabels[i] = new JLabel(players[i].name + " - HP: " + players[i].health);
            info.add(hpLabels[i]);
        }

        log = new JTextArea();
        log.setEditable(false);
        JScrollPane scroll = new JScrollPane(log);
        scroll.setPreferredSize(new Dimension(260, 350));

        rollButton = new JButton("🎲 Roll Dice (" + players[currentPlayer].name + ")");
        rollButton.addActionListener(e -> rollDiceAction());

        JPanel skills = new JPanel(new GridLayout(3, 1, 4, 4));
        for (int i = 0; i < 3; i++) {
            int idx = i + 1;
            skillButtons[i] = new JButton("Skill " + idx);
            skillButtons[i].setEnabled(false);
            int finalI = i;
            skillButtons[i].addActionListener(e -> useSkill(finalI + 1));
            skills.add(skillButtons[i]);
        }

        JButton restart = new JButton("🔄 Restart");
        restart.addActionListener(e -> resetGame());

        JPanel bottom = new JPanel(new BorderLayout(4, 4));
        bottom.add(rollButton, BorderLayout.NORTH);
        bottom.add(skills, BorderLayout.CENTER);
        bottom.add(restart, BorderLayout.SOUTH);

        add(info, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        log("Welcome to Powerpuff Ladders!");
        updateHPLabels();
    }

    private void log(String msg) {
        log.append(msg + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    private void rollDiceAction() {
        if (activeVillain != null) {
            log("Defeat your current enemy first!");
            return;
        }

        PowerpuffGirlF player = players[currentPlayer];
        if (!player.isAlive()) {
            log(player.name + " is down! Skipping turn...");
            nextTurn();
            return;
        }

        int roll = board.rollDice();
        log(player.name + " rolled a " + roll + "!");
        int oldPos = board.playerPosition[currentPlayer];
        int newPos = board.movePlayer(currentPlayer, roll);

        if (board.ladders.containsKey(newPos)) {
            player.health += 10;
            log("🌈 " + player.name + " climbed a ladder to " + newPos + " and healed +10 HP!");
        } else if (board.snakes.containsKey(oldPos + roll)) {
            spawnVillain(player, oldPos + roll);
            boardPanel.refresh();
            return;
        } else if (rng.nextInt(10) < 2) {
            player.takeDamage(5);
            log("💚 The Gangreen Gang appeared! " + player.name + " loses 5 HP!");
        } else {
            log(player.name + " moved to tile " + newPos + ".");
        }

        updateHPLabels();
        boardPanel.refresh();

        if (newPos == board.FINAL_TILE) {
            if (board.mojoAlive) {
                log("\n🧠 Mojo Jojo appears at the final tile with 200 HP!");
                activeVillain = board.mojoBoss;
                enableSkills();
                rollButton.setEnabled(false);
                return;
            } else {
                log("🏆 " + player.name + " reached the final tile and Townsville is safe!");
                rollButton.setEnabled(false);
                return;
            }
        }

        nextTurn();
    }

    private void spawnVillain(PowerpuffGirlF player, int tile) {
        String[] villains = { "Him", "Fuzzy Lumpkins" };
        String name = villains[rng.nextInt(villains.length)];
        int hp = 40 + rng.nextInt(21);
        activeVillain = new VillainF(name, hp, "Normal");
        log("😈 " + player.name + " encountered " + name + " at tile " + tile + " (HP: " + hp + ")!");
        enableSkills();
        rollButton.setEnabled(false);
    }

    private void useSkill(int skillIndex) {
        PowerpuffGirlF player = players[currentPlayer];
        int dmg = player.getSkillDamage(skillIndex);
        activeVillain.takeDamage(dmg);
        log(player.name + " used " + player.skills[skillIndex - 1] + " for " + dmg + " damage! (" + activeVillain.name
                + " HP: " + activeVillain.health + ")");

        if (!activeVillain.isAlive()) {
            log("💥 " + activeVillain.name + " was defeated!");
            player.health += 10;
            log(player.name + " gained +10 HP from victory!");

            if (activeVillain == board.mojoBoss) {
                board.mojoAlive = false;
                log("🔥 Mojo Jojo has been defeated! Townsville is saved!");
                rollButton.setEnabled(false);
            }

            endBattle();
            return;
        }

        int counter = (activeVillain == board.mojoBoss) ? 20 + rng.nextInt(15) : 5 + rng.nextInt(15);
        player.takeDamage(counter);
        log(activeVillain.name + " counterattacks for " + counter + " damage!");
        if (!player.isAlive()) {
            log("💀 " + player.name + " was defeated!");
            endBattle();
        }

        updateHPLabels();
    }

    private void endBattle() {
        disableSkills();
        activeVillain = null;
        rollButton.setEnabled(true);
        updateHPLabels();
        boardPanel.refresh();

        if (!board.mojoAlive) {
            log("🏆 " + players[currentPlayer].name + " defeated Mojo Jojo and saved Townsville!");
            rollButton.setEnabled(false);
        } else {
            nextTurn();
        }
    }

    private void enableSkills() {
        PowerpuffGirlF p = players[currentPlayer];
        for (int i = 0; i < skillButtons.length; i++) {
            skillButtons[i].setText((i + 1) + ". " + p.skills[i]);
            skillButtons[i].setEnabled(true);
        }
    }

    private void disableSkills() {
        for (JButton b : skillButtons)
            b.setEnabled(false);
    }

    private void nextTurn() {
        do {
            currentPlayer = (currentPlayer + 1) % players.length;
        } while (!players[currentPlayer].isAlive());
        rollButton.setText("🎲 Roll Dice (" + players[currentPlayer].name + ")");
    }

    private void resetGame() {
        for (int i = 0; i < players.length; i++) {
            players[i].health = 100;
            board.playerPosition[i] = 1;
        }
        currentPlayer = 0;
        activeVillain = null;
        board.mojoAlive = true;
        board.mojoBoss.health = 200;
        rollButton.setEnabled(true);
        rollButton.setText("🎲 Roll Dice (" + players[currentPlayer].name + ")");
        log.setText("");
        log("New game started!");
        disableSkills();
        updateHPLabels();
        boardPanel.refresh();
    }

    private void updateHPLabels() {
        for (int i = 0; i < players.length; i++) {
            hpLabels[i].setText(
                    players[i].name + " - HP: " + players[i].health + " (Tile " + board.playerPosition[i] + ")");
        }
    }
}
