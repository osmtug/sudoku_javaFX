package sudokuosman.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SudokuGrid {
    private final int SIZE = 9;
    private int[][] grid;
    private int[][] solution;
    private int [][] numberColor; // 0 = normal Color / 1 = blue color for correct guess / -1  = red color for wrong guess
    private Random rand = new Random();
    private int health = 3;

    public SudokuGrid() {
        grid = new int[SIZE][SIZE];
        // Initialiser à 0 (cases vides)
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                grid[i][j] = 0;
        generate();
        solution = copyGrid(grid);
        numberColor = new int[grid.length][grid[0].length]; // 0 default
        removeValues(SudokuOption.getNbEmptyCell(), 1000);
    }

    public int getValue(int row, int col) {
        return grid[row][col];
    }

    public void setValue(int row, int col, int value) {
        grid[row][col] = value;
    }

    public int getSize() {
        return SIZE;
    }

    public List<int[]> getSameNumber(int row, int col){
        List<int[]> res = new ArrayList<int[]>();
        if (row < 0 || row > 8 || col < 0 || col > 8) return res;
        int val = grid[row][col];
        if (val < 1 || val > 9) return res;
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid[i].length; j++){
                if (val == grid[i][j] && numberColor[i][j] != -1){
                    res.add(new int[]{i, j});
                }
            }
        }
        return res;
    }

    public int getNumberColor(int row, int col){ return numberColor[row][col]; }

    public void setNumberColor(int row, int col, int val){ numberColor[row][col] = val; }

    public boolean numberIsComplete(int number){
        if (number == 0) return false;
        int count = 0;
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid[i].length; j++){
                if (number == grid[i][j] && numberColor[i][j] != -1){
                    count++;
                }
            }
        }
        return count == 9;
    }

    public boolean updateNumberColor(int row, int col){
        if (grid[row][col] == solution[row][col]){
            numberColor[row][col] = 1;
            return true;
        }else{
            numberColor[row][col] = -1;
            return false;
        }
    }

    public int getHealth() { return this.health; }

    public void decrementHealth() { this.health--;}

    public List<int[]> getSelectedZone(int row, int col){
        List<int[]> res = new ArrayList<int[]>();
        if (row < 0 || row > 8 || col < 0 || col > 8) return res;
        for (int i = 0; i < grid.length; i++){
            for (int j = 0; j < grid[i].length; j++){
                if (row == i || col == j || (row / 3 == i / 3 && col / 3 == j / 3)){
                    res.add(new int[]{i, j});
                }
            }
        }
        return res;
    }

    // --- Validation d'une valeur dans la grille ---

    public boolean isValid(int row, int col, int value) {
        // Vérifie la ligne
        for (int c = 0; c < SIZE; c++) {
            if (grid[row][c] == value && c != col) {
                return false;
            }
        }

        // Vérifie la colonne
        for (int r = 0; r < SIZE; r++) {
            if (grid[r][col] == value && r != row) {
                return false;
            }
        }

        // Vérifie la sous-grille 3x3
        int boxRowStart = (row / 3) * 3;
        int boxColStart = (col / 3) * 3;
        for (int r = boxRowStart; r < boxRowStart + 3; r++) {
            for (int c = boxColStart; c < boxColStart + 3; c++) {
                if (grid[r][c] == value && (r != row || c != col)) {
                    return false;
                }
            }
        }

        return true;
    }

    // --- Validation complète de la grille ---

    public boolean isGridValid() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                int val = grid[r][c];
                if (val >= 1 && val <= 9 && !isValid(r, c, val)) {
                    return false;
                }
            }
        }
        return true;
    }

    // --- Génération complète d'une grille (backtracking) ---

    public void generate() {
        health = 3;
        generateRecursive(0, 0);
    }

    private boolean generateRecursive(int row, int col) {
        if (row == SIZE) {
            return true; // grille remplie
        }

        int nextRow = (col == SIZE - 1) ? row + 1 : row;
        int nextCol = (col == SIZE - 1) ? 0 : col + 1;

        int[] numbers = shuffledNumbers();

        for (int num : numbers) {
            if (isValid(row, col, num)) {
                grid[row][col] = num;
                if (generateRecursive(nextRow, nextCol)) {
                    return true;
                }
                grid[row][col] = 0;
            }
        }

        return false;
    }

    private int[] shuffledNumbers() {
        int[] nums = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            nums[i] = i + 1;
        }

        for (int i = SIZE - 1; i > 0; i--) {
            int j = rand.nextInt(i + 1);
            int temp = nums[i];
            nums[i] = nums[j];
            nums[j] = temp;
        }

        return nums;
    }

    // --- Suppression de valeurs en s'assurant une unique solution ---

    public void removeValues(int n, int maxTries) {
        List<int[]> cells = new ArrayList<>();

        // Liste de toutes les positions possibles
        for (int row = 0; row < SIZE; row++)
            for (int col = 0; col < SIZE; col++)
                cells.add(new int[]{row, col});

        Collections.shuffle(cells); // Mélanger aléatoirement
        SudokuSolver solver = new SudokuSolver();

        int removed = 0;
        int tries = 0;
        int index = 0;

        while (removed < n && tries < maxTries && index < cells.size()) {
            int[] pos = cells.get(index++);
            int row = pos[0], col = pos[1];
            int backup = grid[row][col];
            if (backup == 0) continue;

            grid[row][col] = 0;
            tries++;

            int solutions = solver.countSolutions(copyGrid(grid));
            if (solutions == 1) {
                removed++;
            } else {
                grid[row][col] = backup; // Rétablir si plusieurs solutions
            }
        }

        System.out.println("Cases retirées : " + removed + " / Tentatives : " + tries);
    }

    private int[][] copyGrid(int[][] original) {
        int[][] copy = new int[SIZE][];
        for (int i = 0; i < SIZE; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }
}

