package sudokuosman.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SudokuGrid {
    private final int SIZE = 9;
    private int[][] grid;
    private Random rand = new Random();

    public SudokuGrid() {
        grid = new int[SIZE][SIZE];
        // Initialiser à 0 (cases vides)
        for (int i = 0; i < SIZE; i++)
            for (int j = 0; j < SIZE; j++)
                grid[i][j] = 0;
        generate();
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
                if (val == grid[i][j]){
                    res.add(new int[]{i, j});
                }
            }
        }
        return res;
    }

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

    public boolean generate() {
        return generateRecursive(0, 0);
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
}

