package sudokuosman.view;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import sudokuosman.model.SudokuOption;

import java.io.IOException;

public class OptionsController {

    public Button btnRetour;
    public VBox vBoxMain;
    public ComboBox<SudokuOption.DifficultyLevel> comboDifficulty;

    @FXML
    private CheckBox darkModeCheckBox;
    @FXML
    private ComboBox<SudokuOption.ColorPalettName> comboPalette;
    private Stage mainStage;

    @FXML
    public void initialize() {
        setStyle();

        darkModeCheckBox.setSelected(SudokuOption.isDarkMode);

        darkModeCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            SudokuOption.isDarkMode = newVal;
            System.out.println("Mode sombre : " + newVal);
            setStyle();
        });

        comboPalette.getItems().addAll(SudokuOption.ColorPalettName.values());
        comboDifficulty.getItems().addAll(SudokuOption.DifficultyLevel.values());

        comboPalette.setValue(SudokuOption.colorPalett);
        comboDifficulty.setValue(SudokuOption.difficultyLevel);

        comboPalette.setOnAction(event -> {
            SudokuOption.colorPalett = comboPalette.getValue();
            setStyle();
        });
        comboDifficulty.setOnAction(event -> {
            SudokuOption.difficultyLevel = comboDifficulty.getValue();
        });
    }

    @FXML
    private void handleRetourAccueil() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sudokuosman/accueil.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) vBoxMain.getScene().getWindow();

            AccueilController controller = loader.getController();
            controller.setStage(stage);

            Scene scene = new Scene(root);

            Image cursorImage = new Image(
                    getClass().getResourceAsStream("/images/cursor.png")
            );
            ImageCursor sudokuCursor = new ImageCursor(
                    cursorImage,
                    0,
                    0
            );

            scene.setCursor(sudokuCursor);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void setStyle(){
        vBoxMain.setStyle("-fx-background-color: "+ SudokuOption.getBGColor() +";");

        setControlStyle(btnRetour);
        setControlStyle(comboPalette);
        setControlStyle(comboDifficulty);
    }

    public void setControlStyle(Control btn){
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

    public void setStage(Stage stage){
        this.mainStage = stage;
        mainStage.setMinHeight(250);
        mainStage.setMinWidth(300);
        mainStage.setHeight(250);
        mainStage.setWidth(300);
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
}
