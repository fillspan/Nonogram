package boundary;

import entities.Cell;
import entities.Nonogram;
import entities.Nonograms;
import main.Solver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Window {
    private JLabel[][] matrix;
    private Solver s;
    private Thread t;
    private Nonogram n;
    private boolean solved = false;

    public Window(Nonogram n, Solver s) {
        JFrame frame = new JFrame("Nonogram solver");
        matrix = new JLabel[n.vertical.length][n.horizontal.length];
        JPanel pnlMain = new JPanel(new GridLayout(n.vertical.length, n.horizontal.length));
        pnlMain.setPreferredSize(new Dimension(n.horizontal.length * 5, n.vertical.length * 5));
        for (int i = 0; i < n.vertical.length; i++)
            for (int j = 0; j < n.horizontal.length; j++) {
                matrix[i][j] = new JLabel();
                matrix[i][j].setOpaque(true);
                if (n.getCell(i, j) == Cell.UNKOWN)
                    matrix[i][j].setBackground(Color.GRAY);
                pnlMain.add(matrix[i][j]);
            }
        frame.add(pnlMain);
        frame.setVisible(true);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addMouseListener(new ClickListener());

        this.s = s;
        this.n = n;
    }

    public void setCell(int row, int col, Cell c) {
        if (c == Cell.FULL)
            matrix[row][col].setBackground(Color.BLACK);
        else if (c == Cell.EMPTY)
            matrix[row][col].setBackground(Color.WHITE);
    }

    public void solveLoop() {
            s.solve();
            solved = true;
            System.out.println(n.toString());
    }

    private void clear() {
        for (JLabel[] matrix1 : matrix)
            for (JLabel jLabel : matrix1)
                jLabel.setBackground(Color.GRAY);
    }

    private class ClickListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (t == null && !solved) {
                t = new Thread(Window.this::solveLoop);
                t.start();
            } else if (solved) {
                n.clear();
                clear();
                solved = false;
                t = null;
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }
}
