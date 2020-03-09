package entities;

import java.util.Arrays;

public class Nonogram {
    public int[][] horizontal;
    public int[][] vertical;
    private Cell[][] matrix;

    public Nonogram(int[][] horizontal, int[][] vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
        matrix = new Cell[vertical.length][horizontal.length];
        for (Cell[] row: matrix)
            Arrays.fill(row, Cell.UNKOWN);
    }

    public void setCell(int row, int col, Cell value) throws Exception {
       if (matrix[row][col] == Cell.UNKOWN)
            matrix[row][col] = value;
        else if (matrix[row][col] != value)
            throw new Exception("Nonogram inconsistency");
    }

    public Cell getCell(int row, int col) {
        return matrix[row][col];
    }

    public void clear() {
        for (Cell[] line : matrix)
            for (int i = 0; i < line.length; i++)
                line[i] = Cell.UNKOWN;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Cell[] line : matrix) {
            for (Cell cv : line)
                sb.append(String.format("%1$s%1$s", cv));
            sb.append("\n");
        }
        return sb.toString();
    }
}
