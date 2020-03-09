package main;

import entities.Nonogram;

public class Main {
    public static void main(String[] args) {
        Nonogram n = Read.get("Balance");
        Solver s = new Solver(n);
    }
}
