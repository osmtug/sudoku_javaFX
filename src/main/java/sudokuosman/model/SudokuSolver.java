package sudokuosman.model;

public class SudokuSolver {
    private int solutionCount;

    public int countSolutions(int[][] grid) {
        solutionCount = 0;
        solve(grid, 0, 0);
        return solutionCount;
    }

    private void solve(int[][] grid, int row, int col) {
        if (row == 9) {
            solutionCount++;
            return;
        }

        if (solutionCount > 1) return; // On peut s'arrêter dès qu'on dépasse 1

        int nextRow = (col == 8) ? row + 1 : row;
        int nextCol = (col + 1) % 9;

        if (grid[row][col] != 0) {
            solve(grid, nextRow, nextCol);
        } else {
            for (int num = 1; num <= 9; num++) {
                if (isValid(grid, row, col, num)) {
                    grid[row][col] = num;
                    solve(grid, nextRow, nextCol);
                    grid[row][col] = 0;
                }
            }
        }
    }

    private boolean isValid(int[][] grid, int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (grid[row][i] == num || grid[i][col] == num) return false;
        }

        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (grid[startRow + i][startCol + j] == num) return false;

        return true;
    }
}

