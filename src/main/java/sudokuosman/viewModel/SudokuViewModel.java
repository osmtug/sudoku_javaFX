package sudokuosman.viewModel;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import sudokuosman.model.SudokuGrid;

import java.util.List;

public class SudokuViewModel {
    private final int SIZE = 9;
    private IntegerProperty[][] cells;
    private SudokuGrid model;

    public SudokuViewModel() {
        model = new SudokuGrid();  // Instance du modèle
        cells = new IntegerProperty[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int row = i;
                int col = j;
                // initialiser la property avec la valeur du modèle
                cells[i][j] = new SimpleIntegerProperty(model.getValue(row, col));

                // Quand on change la propriété, on met à jour le modèle
                cells[i][j].addListener((obs, oldVal, newVal) -> {
                    model.setValue(row, col, newVal.intValue());
                });
            }
        }
    }

    public IntegerProperty cellProperty(int row, int col) {
        return cells[row][col];
    }

    public int getValue(int row, int col) {
        return model.getValue(row, col);
    }

    public void setValue(int row, int col, int value) {
        cells[row][col].set(value);
    }

    public SudokuGrid getModel() {
        return model;
    }

    public void refresh() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                cells[i][j].set(model.getValue(i, j));
            }
        }
    }

    public List<int[]> getSelectedZone(int row, int col){
        return model.getSelectedZone(row, col);
    }

    public List<int[]> getSameNumber(int row, int col){
        return model.getSameNumber(row, col);
    }
}
