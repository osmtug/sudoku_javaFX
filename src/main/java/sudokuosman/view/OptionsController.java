package sudokuosman.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sudokuosman.model.SudokuColor;

import java.io.IOException;

public class OptionsController {

    public Button btnRetour;
    public VBox vBoxMain;

    @FXML
    private CheckBox darkModeCheckBox;
    @FXML
    private ComboBox<SudokuColor.ColorPalettName> comboPalette;

    @FXML
    public void initialize() {
        setStyle();

        darkModeCheckBox.setSelected(SudokuColor.isDarkMode);

        darkModeCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            SudokuColor.isDarkMode = newVal;
            System.out.println("Mode sombre : " + newVal);
            setStyle();
        });

        comboPalette.getItems().addAll(SudokuColor.ColorPalettName.values());

        comboPalette.setValue(SudokuColor.colorPalett);

        comboPalette.setOnAction(event -> {
            SudokuColor.colorPalett = comboPalette.getValue();
            setStyle();
        });
    }

    @FXML
    private void handleRetourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sudokuosman/accueil.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) comboPalette.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setStyle(){
        vBoxMain.setStyle("-fx-background-color: "+ SudokuColor.getBGColor() +";");

        setControlStyle(btnRetour);
        setControlStyle(comboPalette);
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
