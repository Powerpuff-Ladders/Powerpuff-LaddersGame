
import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class ControlPanelF extends JPanel {
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
