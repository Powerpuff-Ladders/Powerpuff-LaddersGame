import javax.swing.*;
import java.awt.*;

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

