package main;

import boundary.Window;
import entities.*;

import java.util.Arrays;
import java.util.Scanner;

public class Solver {
    private Nonogram n;
    private Queue q = new Queue();
    private boolean verbose = false;
    private StringBuilder result = new StringBuilder();
    private int changes = 0;
    private Window w;

    public Solver(Nonogram n) {
        this.n = n;
        w = new Window(n, this);
    }

    public Nonogram solve() {
        queueInitialChecks();
        int counter = 0;
        while (!q.isEmpty()) {
            if (checkLine1(q.remove()))
            counter++;
        }
        System.out.println("Line checks: " + counter);
        return n;
    }

    public Nonogram solveBruteForce() {
        queueInitialChecks();
        while (!q.isEmpty()) {
            checkLine(q.remove());
        }
        return n;
    }

    private void queueInitialChecks() {
        for (int i = 0; i < n.vertical.length; i++) {
            q.add(i, Dir.V);
        }
        for (int i = 0; i < n.horizontal.length; i++)
            q.add(i, Dir.H);
    }


    public boolean checkLine(Pointer p) {
        boolean changed = false, oneCorrect = false;
        int length = p.direction == Dir.H ? n.vertical.length : n.horizontal.length;

        Cell[] lineEmpty = new Cell[length];
        Cell[] lineFull = new Cell[length];
        Cell[] newLine = new Cell[length];

        int[] clusters = p.direction == Dir.H ? n.horizontal[p.index] : n.vertical[p.index];

        ClusterMoves cm = null;
        try {
            cm = new ClusterMoves(clusters, length);
        } catch (
                Exception e) {
            e.printStackTrace();
        }

        int largestSpace = 0, temp = 0;
        if (p.direction == Dir.H) {
            for (int i = 0; i < n.vertical.length; i++) {
                if (n.getCell(i, p.index) != Cell.FULL) {
                    temp++;
                } else {
                    if (temp > largestSpace)
                        largestSpace = temp;
                    temp = 0;
                }
            }
        } else {
            for (int i = 0; i < n.horizontal.length; i++) {
                if (n.getCell(p.index, i) != Cell.FULL) {
                    temp++;
                } else {
                    if (temp > largestSpace)
                        largestSpace = temp;
                    temp = 0;
                }
            }
        }
        if (temp > largestSpace)
            largestSpace = temp;

        int largestCluster = 0;
        for (
                int i : clusters)
            if (i > largestCluster)
                largestCluster = i;

        if (largestSpace - cm.getLeastLength() <= largestCluster) {

            checkLines:
            do {
                // Generate a guessed line
                int index = 0;
                for (int i = 0; i < clusters.length; i++) {
                    if (i != 0) // Put an EMPTY Cell between clusters
                        newLine[index++] = Cell.EMPTY;
                    for (int j = 0; j < cm.getMove(i) - (i == 0 ? 0 : cm.getMove(i - 1)); j++) // Fill with EMPTY cells corresponding to the moved cluster
                        newLine[index++] = Cell.EMPTY;
                    for (int j = 0; j < clusters[i]; j++) // Fill in the cluster
                        newLine[index++] = Cell.FULL;
                }
                for (int i = index; i < length; i++) // If there's space left in the line, fill with EMPTY
                    newLine[i] = Cell.EMPTY;

                // Check consistency (abort try if a Cell is set in the final matrix but the value doesn't correspond to the guessed value)
                for (int i = 0; i < length; i++) {
                    if ((p.direction == Dir.H ? n.getCell(i, p.index) : n.getCell(p.index, i)) != Cell.UNKOWN &&
                            (p.direction == Dir.H ? n.getCell(i, p.index) : n.getCell(p.index, i)) != newLine[i])
                        continue checkLines;
                }

                if (verbose) {
                    for (Cell c : newLine)
                        System.out.print("[" + c + "]");
                    System.out.println();
                }
                oneCorrect = true;

                // Transfer knowns (Collect all known FULL in lineFull and all known EMPTY in lineEmpty)
                for (int i = 0; i < length; i++) {
                    if (newLine[i] == Cell.FULL)
                        lineFull[i] = Cell.FULL;
                    if (newLine[i] == Cell.EMPTY)
                        lineEmpty[i] = Cell.EMPTY;
                }
            } while (cm.next()); // Generate next possible alignment of the clusters

            /*if (verbose) {
                for (Cell c : lineFull)
                    System.out.print("[" + (c == null ? "?" : c) + "]");
                System.out.println();
                for (Cell c : lineEmpty)
                    System.out.print("[" + (c == null ? "?" : c) + "]");
                System.out.println();
            }*/
            try {
                changed = transfer(lineFull, Cell.EMPTY, p); // lineFull contains all possible FULL cells, so others must be EMPTY
                changed |= transfer(lineEmpty, Cell.FULL, p); // lineEmpty contains all possible EMPTY cells, so others must be FULL
            } catch (Exception e) {
                System.out.println(e);
            }
            //scanner.nextLine();
        }
        return changed;
    }

    public boolean checkLine1(Pointer p) {
        result.setLength(0);

        result.append(p + "\n");

        Cell[] orgLine = getLine(p);
        int[] clusters = getClusters(p);

        boolean changed = false;
        try {
            Cell[] fullLine = checkLine2(orgLine, clusters);
            if (!Arrays.equals(orgLine, fullLine))
                changed = transfer2(fullLine, p);
        } catch (Exception e) {
            System.out.println("Couldn't complete line check on " + p + ", gathered output:");
            System.out.println(result);
            e.printStackTrace();
        }

        result.append("N: ");
        for (int i = 0; i < orgLine.length; i++)
            if (p.direction == Dir.H)
                result.append(String.format("[%s]", n.getCell(i, p.index)));
            else
                result.append(String.format("[%s]", n.getCell(p.index, i)));

        if (changed && verbose) {
            System.out.println(result);
            System.out.println("Changes: " + ++changes + "\n");
        }
        return changed;
    }

    private Cell[] getLine(Pointer p) {
        int length = p.direction == Dir.H ? n.vertical.length : n.horizontal.length;

        Cell[] orgLine = new Cell[length];

        for (int i = 0; i < length; i++)
            if (p.direction == Dir.H)
                orgLine[i] = n.getCell(i, p.index);
            else
                orgLine[i] = n.getCell(p.index, i);

        return orgLine;
    }

    private int[] getClusters(Pointer p) {
        return p.direction == Dir.H ? n.horizontal[p.index] : n.vertical[p.index];
    }


    public Cell[] checkLine2(Cell[] orgLine, int[] clusters) {
        Cell[] fullLine = new Cell[orgLine.length];
        Arrays.fill(fullLine, Cell.UNKOWN);

        printIntArray("Clusters", clusters);
        result.append("   ");
        for (int i = 0; i < orgLine.length; i++)
            result.append(String.format("%02d ", i));
        result.append("\n");

        if (verbose)
            printCellArray("O", orgLine);

        int[] forwLine, backLine;
        forwLine = checkLine3(orgLine, clusters);

        if (verbose)
            printIntArray("F", forwLine);

        // Reverse and run backwards
        reverseCellArray(orgLine);
        reverseIntArray(clusters);
        //printCellArray("R", orgLine);
        backLine = checkLine3(orgLine, clusters);

        // Twist back
        reverseIntArray(clusters);
        reverseCellArray(orgLine);
        //printIntArray("B", backLine);
        reverseIntArray(backLine);
        // Reverse cluster index in line
        for (int i = 0; i < backLine.length; i++)
            if (backLine[i] != 0)
                backLine[i] = clusters.length - backLine[i] + 1;

        if (verbose)
            printIntArray("B", backLine);

        // Fill all known FULL Cells
        for (int i = 0; i < forwLine.length; i++)
            if (forwLine[i] != 0 && forwLine[i] == backLine[i])
                fullLine[i] = Cell.FULL;

        // Fill in all EMPTY Cells that exists between the same clusters in forwLine and backLine
        for (int i = 0; i < orgLine.length; i++) {
            if (backLine[i] == 0 && forwLine[i] == 0) {
                int forwRightLimit = -1, backRightLimit = -1;
                for (int j = i; j < orgLine.length; j++) {
                    if (forwLine[j] != 0 && forwRightLimit == -1)
                        forwRightLimit = forwLine[j];
                    if (backLine[j] != 0 && backRightLimit == -1)
                        backRightLimit = backLine[j];
                }
                int forwLeftLimit = -1, backLeftLimit = -1;
                for (int j = i; j >= 0; j--) {
                    if (forwLine[j] != 0 && forwLeftLimit == -1)
                        forwLeftLimit = forwLine[j];
                    if (backLine[j] != 0 && backLeftLimit == -1)
                        backLeftLimit = backLine[j];
                }
                if (forwLeftLimit == backLeftLimit && forwRightLimit == backRightLimit)
                    fullLine[i] = Cell.EMPTY;
            }
        }

        // Check if the clusters occupying a grouping covers it, and all other possible clusters do too
        int gIndex = 2;
        while (gIndex < orgLine.length - 3 && !(orgLine[gIndex] == Cell.FULL && forwLine[gIndex - 1] == 0))
            gIndex++;
        if (gIndex <= orgLine.length - 3) {
            int gSize = 1;
            while (gIndex + gSize < orgLine.length && orgLine[gIndex + gSize] == Cell.FULL)
                gSize++;
            if (gIndex + gSize - 1 < orgLine.length - 3) {
                boolean same = true;
                for (int i = backLine[gIndex] - 1; i <= forwLine[gIndex] - 1; i++)
                    if (gSize != clusters[i])
                        same = false;
                if (same) {
                    fullLine[gIndex - 1] = Cell.EMPTY;
                    fullLine[gIndex + gSize] = Cell.EMPTY;
                }
            }
        }
        if (verbose)
            printCellArray("C", fullLine);

        return fullLine;
    }

    public int[] checkLine3(Cell[] orgLine, int[] clusters) {

        int[] numLine = new int[orgLine.length];
        //Arrays.fill(numLine, -1);

        int index = 0, gIndex, gSize, sIndex;

        // First place clusters as far left as possible
        placeClusters(index, clusters, 0, orgLine, numLine);
        boolean changed;
        // Then check if there are FULL Cells beyond the clusters
        do {
            changed = false;
            index = orgLine.length - 1;
            int cIndex = clusters.length - 1;
            while (index > 0) {
                // See if there's a FULL Cell with no cluster attached
                while (index > 0 && !(numLine[index] == 0 && orgLine[index] == Cell.FULL)) {
                    index--;
                    if (numLine[index] > 0)
                        // Which cluster should be there (-1 because cluster 0 is marked 1, 1 is 2, and -1 because it's the previous cluster that should be ther
                        cIndex = numLine[index] - 2;
                }
                if (index > 0) { // If index is larger than 0, an errant Cell has been found
                    gIndex = index;
                    gSize = 1;
                    while (orgLine[gIndex - 1] == Cell.FULL) {
                        gIndex--;
                        gSize++;
                    }

                    while (clusters[cIndex] < gSize)
                        cIndex--;

                    sIndex = gIndex;
                    while (orgLine[sIndex - 1] != Cell.EMPTY && (gIndex + gSize - 1) - sIndex + 1 < clusters[cIndex])
                        sIndex--;

                    clearClusters(cIndex, numLine);
                    placeClusters(sIndex, clusters, cIndex, orgLine, numLine);

                    index = gIndex + gSize - 1 - clusters[cIndex];
                    cIndex--;
                    changed = true;
                }
            }
        } while (changed);

        return numLine;
    }

    private void placeClusters(int index, int[] clusters, int cStart, Cell[] orgLine, int[] forwLine) {
        for (int i = cStart; i < clusters.length; i++) {
            if (i != cStart)
                index++;

            while (checkSpace(index, clusters[i], orgLine))
                index++;

            for (int j = 0; j < clusters[i]; j++)
                forwLine[index++] = i + 1;
        }
    }

    private void clearClusters(int cluster, int[] forwLine) {
        for (int i = 0; i < forwLine.length; i++)
            if (forwLine[i] >= (cluster + 1))
                forwLine[i] = 0;
    }

    private boolean checkSpace(int index, int cluster, Cell[] orgLine) {
        return noSpaceFront(index, orgLine) ||
                noSpaceBack(index, cluster, orgLine) ||
                coversEmpty(index, cluster, orgLine);
    }

    public boolean noSpaceFront(int index, Cell[] orgLine) {
        return !(index == 0 || orgLine[index - 1] != Cell.FULL);
    }

    private boolean noSpaceBack(int index, int cluster, Cell[] orgLine) {
        return !(index + cluster >= orgLine.length || orgLine[index + cluster] != Cell.FULL);
    }

    private boolean coversEmpty(int index, int cluster, Cell[] orgLine) {
        for (int j = 0; j < cluster; j++)
                if (orgLine[j + index] == Cell.EMPTY)
                    return true;
        return false;
    }

    private void reverseCellArray(Cell[] array) {
        Cell temp;
        for (int i = 0; i < array.length / 2; i++) {
            temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
    }

    private void reverseIntArray(int[] array) {
        int temp;
        for (int i = 0; i < array.length / 2; i++) {
            temp = array[i];
            array[i] = array[array.length - i - 1];
            array[array.length - i - 1] = temp;
        }
    }

    private void printIntArray(String info, int[] array) {
        result.append(info + ": ");
        for (int c : array)
            result.append("[" + (c == 0 ? " " : c) + "]");
        result.append("\n");
    }

    private void printCellArray(String info, Cell[] array) {
        result.append(info + ": ");
        for (Cell c : array)
            result.append("[" + c + "]");
        result.append("\n");
    }

    private boolean transfer2(Cell[] from, Pointer p) throws Exception {
        boolean changed = false;
        Cell orgCell;
        for (int i = 0; i < from.length; i++) {
            orgCell = (p.direction == Dir.H ? n.getCell(i, p.index) : n.getCell(p.index, i));
            if (from[i] != Cell.UNKOWN && orgCell == Cell.UNKOWN) {
                if (p.direction == Dir.H) {
                    n.setCell(i, p.index, from[i]);
                    w.setCell(i, p.index, from[i]);
//                    if (verbose)
//                        System.out.println(testConsistency(i, Dir.V));
                    q.add(i, Dir.V);
//                    if (verbose)
//                        System.out.println(i + ", " + p.index + ",  [" + from[i] + "]");
                } else {
                    n.setCell(p.index, i, from[i]);
                    w.setCell(p.index, i, from[i]);
//                    if (verbose)
//                        System.out.println(testConsistency(i, Dir.H));
                    q.add(i, Dir.H);
//                    if (verbose)
//                        System.out.println(p.index + ", " + i + ", [" + from[i] + "]");
                }
                changed = true;
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else if (orgCell != Cell.UNKOWN && from[i] != Cell.UNKOWN && from[i] != orgCell)
                System.out.println("Inconsistency on line " + p.index + ", dir " + p.direction + ", cell " + i + ": trying to set " + from[i] + " where already " + orgCell);
        }
        return changed;
    }

    public boolean testConsistency(int i, Dir d) {
        try {
            Pointer p = new Pointer(i, d);
            int[] clusters = getClusters(p);
            Cell[] line = getLine(p);

            placeClusters(0, clusters, 0, line, new int[line.length]);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean transfer(Cell[] from, Cell value, Pointer p) throws Exception {
        boolean changed = false;
        for (int i = 0; i < from.length; i++) {
            if (from[i] == null && (p.direction == Dir.H ? n.getCell(i, p.index) : n.getCell(p.index, i)) == null) {
                if (p.direction == Dir.H) {
                    n.setCell(i, p.index, value);
                    q.add(i, Dir.V);
                    //if (verbose)
                    //System.out.println(i + ", " + p.index + ",  [" + value + "]");
                } else {
                    n.setCell(p.index, i, value);
                    q.add(i, Dir.H);
                    //if (verbose)
                    //System.out.println(p.index + ", " + i + ", [" + value + "]");
                }
                changed = true;
            }
        }
        return changed;
    }
}
