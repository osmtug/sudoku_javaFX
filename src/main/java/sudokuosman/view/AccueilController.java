package sudokuosman.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sudokuosman.model.SudokuColor;

import java.io.IOException;

public class AccueilController {

    public VBox vBoxMain;
    @FXML
    private Button btnJouer;

    @FXML
    private Button btnOptions;

    @FXML
    private Button btnQuitter;

    @FXML
    public void initialize() {
        setStyle();

        btnJouer.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sudokuosman/SudokuView.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();

                stage.setScene(new Scene(root));

                stage.sizeToScene();

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        btnOptions.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sudokuosman/Option.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();

                stage.setScene(new Scene(root));

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        btnQuitter.setOnAction(e -> {
            // Fermer la fenêtre principale
            Stage stage = (Stage) btnQuitter.getScene().getWindow();
            stage.close();
        });
    }

    public void setStyle(){
        vBoxMain.setStyle("-fx-background-color: "+ SudokuColor.getBGColor() +";");

        setButtonStyle(btnJouer);
        setButtonStyle(btnOptions);
        setButtonStyle(btnQuitter);
    }

    public void setButtonStyle(Button btn){
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
