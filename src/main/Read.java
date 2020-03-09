package main;

import entities.Nonogram;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class Read {
    public static Nonogram get(String file) {
        Nonogram n= null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("files/" + file + ".non")))) {
            String line;
            int[][] horizontal;
            int[][] vertical;
            int c = 0, r = 0, index = 0;

            do {
                line = br.readLine();
                if (line.startsWith("width"))
                    c = Integer.parseInt(line.split(" ")[1]);
                if (line.startsWith("height"))
                    r = Integer.parseInt(line.split(" ")[1]);
            }
            while (!line.equals("rows"));

            horizontal = new int[c][];
            vertical = new int[r][];


            String[] lines;
            do {
                line = br.readLine();
                if (!line.equals("columns") && !line.isEmpty() && !line.isBlank()) {
                    lines = line.split(",");
                    int[] clusters = new int[lines.length];
                    for (int i = 0; i < clusters.length; i++)
                        clusters[i] = Integer.parseInt(lines[i]);
                    vertical[index++] = clusters;
                }
            } while (!line.equals("columns"));

            index = 0;
            do {
                line = br.readLine();
                if (line != null && !line.isEmpty() && !line.isBlank()) {
                    lines = line.split(",");
                    int[] clusters = new int[lines.length];
                    for (int i = 0; i < clusters.length; i++)
                        clusters[i] = Integer.parseInt(lines[i]);
                    horizontal[index++] = clusters;
                }
            } while (line != null);

            n = new Nonogram(horizontal, vertical);

        } catch (Exception e) {
            System.out.println(e);
        }
        return n;
    }
}