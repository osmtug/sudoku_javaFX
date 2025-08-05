package sudokuosman.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import sudokuosman.model.SudokuOption;

import java.io.IOException;

@SuppressWarnings("CallToPrintStackTrace")
public class AccueilController {

    public Stage mainStage;

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

                SudokuController controller = loader.getController();
                controller.setStage(stage);

                stage.setScene(new Scene(root));

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        btnOptions.setOnAction(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sudokuosman/Option.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) ((javafx.scene.Node) e.getSource()).getScene().getWindow();

                OptionsController controller = loader.getController();
                controller.setStage(stage);

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
        vBoxMain.setStyle("-fx-background-color: "+ SudokuOption.getBGColor() +";");

        setButtonStyle(btnJouer);
        setButtonStyle(btnOptions);
        setButtonStyle(btnQuitter);
    }

    public void setButtonStyle(Button btn){
        btn.setStyle(
                "-fx-background-color: "+ SudokuOption.getselectedZoneColor() +";" +
                        "-fx-text-fill: "+ SudokuOption.getNumberColor() +";" +
                        "-fx-font-weight: bold;" +
                        "-fx-border-color: "+ SudokuOption.getNumberColor() +";" +
                        "-fx-border-width: 2px;" +
                        "-fx-border-radius: 5px;" +
                        "-fx-background-radius: 5px;"
        );

        btn.setOnMouseEntered(e -> {
            btn.setStyle(
                    "-fx-background-color: "+ SudokuOption.getSelectedColor() +";" +
                            "-fx-text-fill: "+ SudokuOption.getNumberColor() +";" +
                            "-fx-font-weight: bold;" +
                            "-fx-border-color: "+ SudokuOption.getNumberColor() +";" +
                            "-fx-border-width: 2px;" +
                            "-fx-border-radius: 5px;" +
                            "-fx-background-radius: 5px;"
            );
            playSpringAnimation(btn, true);
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(
                    "-fx-background-color: "+ SudokuOption.getselectedZoneColor() +";" +
                            "-fx-text-fill: "+ SudokuOption.getNumberColor() +";" +
                            "-fx-font-weight: bold;" +
                            "-fx-border-color: "+ SudokuOption.getNumberColor() +";" +
                            "-fx-border-width: 2px;" +
                            "-fx-border-radius: 5px;" +
                            "-fx-background-radius: 5px;"
            );
            playSpringAnimation(btn, false);
        });
    }

    private void playSpringAnimation(Control button, boolean grow) {
        Timeline timeline = new Timeline();

        double[] frames = grow
                ? new double[]{1.0, 1.15, 1.05, 1.1}
                : new double[]{1.1, 0.9, 1.05, 1.0};

        Duration total = Duration.millis(300);
        double step = total.toMillis() / (frames.length - 1);

        for (int i = 0; i < frames.length; i++) {
            Duration time = Duration.millis(i * step);
            timeline.getKeyFrames().addAll(
                    new KeyFrame(time,
                            new KeyValue(button.scaleXProperty(), frames[i]),
                            new KeyValue(button.scaleYProperty(), frames[i]))
            );
        }

        timeline.play();
    }

    public void setStage(Stage stage){
        this.mainStage = stage;
        mainStage.setMinHeight(226);
        mainStage.setMinWidth(276);
        mainStage.setHeight(226);
        mainStage.setWidth(276);
    }
}
