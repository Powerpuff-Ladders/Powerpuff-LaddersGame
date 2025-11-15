
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class BoardPanelF extends JPanel {
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
        int cols = 10, rows = 7, w = getWidth(), h = getHeight(), cellW = w / cols, cellH = h / rows;

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
