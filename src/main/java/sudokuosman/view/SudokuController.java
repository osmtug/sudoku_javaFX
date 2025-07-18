package sudokuosman.view;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sudokuosman.model.SudokuColor;
import sudokuosman.viewModel.SudokuViewModel;

import java.io.IOException;
import java.util.List;

public class SudokuController {

    public VBox vBoxMain;
    public Button btnRetour;
    @FXML
    private GridPane gridPane;

    @FXML
    private HBox numberButtons;

    private SudokuViewModel viewModel;

    private final int SIZE = 9;
    private Label[][] labels;

    private int selectedRow = -1;
    private int selectedCol = -1;


    public void initialize() {
        viewModel = new SudokuViewModel();
        labels = new Label[SIZE][SIZE];

        btnRetour.setOnMouseClicked(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sudokuosman/accueil.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) vBoxMain.getScene().getWindow();
                stage.setScene(new Scene(root));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        vBoxMain.setOnMouseClicked(event -> {
            selectCell(-1, -1);
        });

        // Création des labels et binding unidirectionnel
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Label label = new Label();
                label.setMinSize(20, 20);
                label.setMaxSize(60, 60);
                label.setPrefSize(80, 80);

                int r = row, c = col;

                // Binding unidirectionnel: label texte suit la propriété VM
                label.textProperty().bind(
                        Bindings.createStringBinding(() -> {
                            int val = viewModel.cellProperty(r, c).get();
                            return val == 0 ? "" : String.valueOf(val);
                        }, viewModel.cellProperty(r, c))
                );

                // Clic pour sélectionner la cellule
                label.setOnMouseClicked(e -> {
                    selectCell(r, c);
                    e.consume();
                });

                labels[row][col] = label;
                gridPane.add(label, col, row);
            }
        }

        // Création des boutons 1-9
        for (int i = 1; i <= 9; i++) {
            Button btn = new Button(String.valueOf(i));
            btn.setMinSize(30, 30);
            btn.setMaxSize(40, 40);
            btn.setPrefSize(40, 40);
            btn.setOnAction(e -> {
                if (selectedRow != -1 && selectedCol != -1) {
                    int val = Integer.parseInt(btn.getText());
                    viewModel.setValue(selectedRow, selectedCol, val);
                    setStyle();
                }

                Scene scene = ((Node) e.getSource()).getScene();

                // Récupérer la fenêtre (stage) depuis la scène
                Stage stage = (Stage) scene.getWindow();

                // Récupérer largeur et hauteur de la fenêtre
                double width = stage.getWidth();
                double height = stage.getHeight();

            });
            numberButtons.getChildren().add(btn);
        }

        setStyle();
    }

    private void setStyle() {

        setControlStyle(btnRetour);
        vBoxMain.setStyle("-fx-background-color: "+ SudokuColor.getBGColor() +";");
        for(int i = 0; i < SIZE; i++){
            setControlStyle((Control) numberButtons.getChildren().get(i));
            for (int j = 0; j < SIZE; j++){

                labels[i][j].setStyle(
                        "-fx-background-color: "+ SudokuColor.getBGColor() +";" +
                                "-fx-text-fill: "+ SudokuColor.getNumberColor() +";" +
                                "-fx-font-weight: bold;" +
                                " -fx-alignment: center;" +
                                getBorderThickness(i, j) +
                                "-fx-font-size: 18;" +
                                "-fx-border-color: "+ SudokuColor.getNumberColor() +";" +
                                "-fx-border-radius: 5px;" +
                                "-fx-background-radius: 5px;"
                );
            }
        }
        List<int[]> selectedZone = viewModel.getSelectedZone(selectedRow, selectedCol);
        for (int i = 0; i < selectedZone.size(); i++){
            int row = selectedZone.get(i)[0];
            int col = selectedZone.get(i)[1];

            labels[row][col].setStyle(
                    "-fx-background-color: "+ SudokuColor.getselectedZoneColor() +";" +
                            "-fx-text-fill: "+ SudokuColor.getNumberColor() +";" +
                            "-fx-font-weight: bold;" +
                            " -fx-alignment: center;" +
                            "-fx-font-size: 18;" +
                            getBorderThickness(row, col) +
                            "-fx-border-color: "+ SudokuColor.getNumberColor() +";" +
                            "-fx-border-radius: 5px;" +
                            "-fx-background-radius: 5px;"
            );
        }

        List<int[]> sameNumber = viewModel.getSameNumber(selectedRow, selectedCol);
        for (int i = 0; i < sameNumber.size(); i++){
            int row = sameNumber.get(i)[0];
            int col = sameNumber.get(i)[1];
            labels[row][col].setStyle(
                    "-fx-background-color: "+ SudokuColor.getSameNumberColor() +";" +
                            "-fx-text-fill: "+ SudokuColor.getNumberColor() +";" +
                            "-fx-font-weight: bold;" +
                            " -fx-alignment: center;" +
                            "-fx-font-size: 18;" +
                            getBorderThickness(row, col) +
                            "-fx-border-color: "+ SudokuColor.getNumberColor() +";" +
                            "-fx-border-radius: 5px;" +
                            "-fx-background-radius: 5px;"
            );
        }

        if (selectedRow < 0 || selectedRow > SIZE || selectedCol < 0 || selectedCol > SIZE) return;
        labels[selectedRow][selectedCol].setStyle(
                "-fx-background-color: "+ SudokuColor.getSelectedColor() +";" +
                        "-fx-text-fill: "+ SudokuColor.getNumberColor() +";" +
                        "-fx-font-weight: bold;" +
                        " -fx-alignment: center;" +
                        "-fx-font-size: 18;" +
                        getBorderThickness(selectedRow, selectedCol) +
                        "-fx-border-color: "+ SudokuColor.getNumberColor() +";" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;"
        );

    }

    private String getBorderThickness(int row, int col){
        String top = (row % 3 == 0) ? "2px" : "0.5px";
        String left = (col % 3 == 0) ? "2px" : "0.5px";
        String right = ((col + 1) % 3 == 0) ? "2px" : "0.5px";
        String bottom = ((row + 1) % 3 == 0) ? "2px" : "0.5px";
        return "-fx-border-width: " + top + " " + right + " " + bottom + " " + left + ";";
    }

    private void selectCell(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        setStyle();
    }

    private void updateSelectedCellColor() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (r == selectedRow && c == selectedCol) {
                    labels[r][c].setStyle("-fx-font-size: 18;" +
                            " -fx-border-color: black;" +
                            " -fx-alignment: center;" +
                            " -fx-background-color: rgb(20,123,168);" +
                            " -fx-text-fill: white;");
                } else {
                    labels[r][c].setStyle("-fx-font-size: 18;" +
                            " -fx-border-color: black;" +
                            " -fx-alignment: center;" +
                            " -fx-background-color: white;" +
                            " -fx-text-fill: black;");
                }
            }
        }
    }

    public void setControlStyle(Control btn){
        btn.setStyle(
                "-fx-background-color: "+ SudokuColor.getselectedZoneColor() +";" +
                        "-fx-text-fill: "+ SudokuColor.getNumberColor() +";" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: "+ SudokuColor.getNumberColor() +";" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;"
        );

        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                    "-fx-background-color: "+ SudokuColor.getSelectedColor() +";" +
                            "-fx-text-fill: "+ SudokuColor.getNumberColor() +";" +
                            "-fx-font-weight: bold;" +
                            "-fx-border-color: "+ SudokuColor.getNumberColor() +";" +
                            "-fx-border-width: 2px;" +
                            "-fx-border-radius: 5px;" +
                            "-fx-background-radius: 5px;"
            );
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(
                    "-fx-background-color: "+ SudokuColor.getselectedZoneColor() +";" +
                            "-fx-text-fill: "+ SudokuColor.getNumberColor() +";" +
                            "-fx-font-weight: bold;" +
                            "-fx-border-color: "+ SudokuColor.getNumberColor() +";" +
                            "-fx-border-width: 2px;" +
                            "-fx-border-radius: 5px;" +
                            "-fx-background-radius: 5px;"
            );
        });
    }
}
