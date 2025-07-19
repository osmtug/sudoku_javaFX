package sudokuosman.view;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;
import sudokuosman.model.SudokuOption;
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

    @FXML private Circle thumb;
    @FXML private StackPane switchContainer;
    @FXML private Rectangle track;
    @FXML private Label normalLabel;
    @FXML private Label annotationLabel;

    public boolean isAnnotation = false;

    private SudokuViewModel viewModel;

    private final int SIZE = 9;
    private Label[][] labels;
    private Button[] buttons;

    private int selectedRow = -1;
    private int selectedCol = -1;

    private Stage stage;


    public void initialize() {
        viewModel = new SudokuViewModel();
        labels = new Label[SIZE][SIZE];
        buttons = new Button[SIZE+1];
        vBoxMain.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) { setValue(0); }
            if (event.getCode() == KeyCode.SPACE) { return; }
            String key = event.getText();

            if (key.matches("[1-9]")) {
                if (selectedRow != -1 && selectedCol != -1) {
                    int val = Integer.parseInt(key);
                    setValue(val);
                }
            }
        });

        switchContainer.setOnMouseClicked(e -> toggleSwitch());

        btnRetour.setOnMouseClicked(event -> {
            backToHome();
        });

        vBoxMain.setOnMouseClicked(event -> {
            selectCell(-1, -1);
        });

        // Création des labels et binding unidirectionnel
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                Label label = new Label();
                label.setMinSize(20, 20);
                label.setMaxSize(100, 100);
                label.setPrefSize(100, 100);

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

                label.setOnMouseEntered(e -> {
                    playSpringAnimation(label, true);
                    label.toFront();
                });

                label.setOnMouseExited(e -> playSpringAnimation(label, false));

                labels[row][col] = label;
                gridPane.add(label, col, row);
            }
        }

        // Création des boutons 1-9
        for (int i = 0; i <= 9; i++) {
            Button btn = createButton(i);
            buttons[i] = btn;
            numberButtons.getChildren().add(btn);
        }

        setStyle();
    }

    private Button createButton(int i) {
        Button btn = new Button(String.valueOf(i));


        btn.setMinSize(30, 30);
        btn.setMaxSize(40, 40);
        btn.setPrefSize(40, 40);
        btn.setOnAction(e -> {
            if (selectedRow != -1 && selectedCol != -1) {
                int val = Integer.parseInt(btn.getText());
                setValue(val);
            }

            Scene scene = ((Node) e.getSource()).getScene();

            // Récupérer la fenêtre (stage) depuis la scène
            Stage stage = (Stage) scene.getWindow();

            // Récupérer largeur et hauteur de la fenêtre
            double width = stage.getWidth();
            double height = stage.getHeight();

        });
        return btn;
    }

    private void setStyle() {

        switchContainer.setStyle("-fx-background-color: "+ SudokuOption.getselectedZoneColor() +"; " +
                "-fx-background-radius: 30; -fx-border-color: "+ SudokuOption.getNumberColor() +";" +
                "-fx-border-radius: 30px");
        track.setFill(Color.rgb(SudokuOption.getselectedZoneColor().getR(), SudokuOption.getselectedZoneColor().getG(), SudokuOption.getselectedZoneColor().getB()));
        thumb.setFill(Color.rgb(SudokuOption.getSelectedColor().getR(), SudokuOption.getSelectedColor().getG(), SudokuOption.getSelectedColor().getB()));

        setControlStyle(btnRetour);
        vBoxMain.setStyle("-fx-background-color: "+ SudokuOption.getBGColor() +";");

        for(int i = 0; i < numberButtons.getChildren().size(); i++){
            if (numberButtons.getChildren().get(i) instanceof Button) {
                setControlStyle((Control) numberButtons.getChildren().get(i));
            }
        }

        for(int i = 0; i < SIZE; i++){

            for (int j = 0; j < SIZE; j++){

                labels[i][j].setStyle(
                        "-fx-background-color: "+ SudokuOption.getBGColor() +";" +
                                "-fx-text-fill: "+ getNumberColor(i, j) +";" +
                                "-fx-font-weight: bold;" +
                                " -fx-alignment: center;" +
                                getBorderThickness(i, j) +
                                "-fx-font-size: 18;" +
                                "-fx-border-color: "+ SudokuOption.getNumberColor() +";" +
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
                    "-fx-background-color: "+ SudokuOption.getselectedZoneColor() +";" +
                            "-fx-text-fill: "+ getNumberColor(row, col) +";" +
                            "-fx-font-weight: bold;" +
                            " -fx-alignment: center;" +
                            "-fx-font-size: 18;" +
                            getBorderThickness(row, col) +
                            "-fx-border-color: "+ SudokuOption.getNumberColor() +";" +
                            "-fx-border-radius: 5px;" +
                            "-fx-background-radius: 5px;"
            );
        }

        List<int[]> sameNumber = viewModel.getSameNumber(selectedRow, selectedCol);
        for (int i = 0; i < sameNumber.size(); i++){
            int row = sameNumber.get(i)[0];
            int col = sameNumber.get(i)[1];
            labels[row][col].setStyle(
                    "-fx-background-color: "+ SudokuOption.getSameNumberColor() +";" +
                            "-fx-text-fill: "+ getNumberColor(row, col) +";" +
                            "-fx-font-weight: bold;" +
                            " -fx-alignment: center;" +
                            "-fx-font-size: 18;" +
                            getBorderThickness(row, col) +
                            "-fx-border-color: "+ SudokuOption.getNumberColor() +";" +
                            "-fx-border-radius: 5px;" +
                            "-fx-background-radius: 5px;"
            );
        }

        if (selectedRow < 0 || selectedRow > SIZE || selectedCol < 0 || selectedCol > SIZE) return;
        labels[selectedRow][selectedCol].setStyle(
                "-fx-background-color: "+ SudokuOption.getSelectedColor() +";" +
                        "-fx-text-fill: "+ getNumberColor(selectedRow, selectedCol) +";" +
                        "-fx-font-weight: bold;" +
                        " -fx-alignment: center;" +
                        "-fx-font-size: 18;" +
                        getBorderThickness(selectedRow, selectedCol) +
                        "-fx-border-color: "+ SudokuOption.getNumberColor() +";" +
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

    private void playSpringAnimation(Control button, boolean grow) {
        Timeline timeline = new Timeline();

        double[] frames = grow
                ? new double[]{1.0, 1.25, 1.1, 1.15}
                : new double[]{1.15, 0.9, 1.05, 1.0};

        Duration total = Duration.millis(400);
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

    private String getNumberColor(int row, int col){
        switch (viewModel.getNumberColor(row, col)){
            case 1 :
                return "blue";
            case -1:
                return "red";
            default:
                return SudokuOption.getNumberColor().toString();
        }
    }

    private void setValue(int val){
        viewModel.setValue(selectedRow, selectedCol, val);
        if (viewModel.numberIsComplete(val)){
            buttons[val].setDisable(true);
        }
        setStyle();
    }

    private void toggleSwitch() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(200), thumb);
        if (isAnnotation) {
            transition.setToX(-10);
        } else {
            transition.setToX(10);
        }
        isAnnotation = !isAnnotation;
        transition.play();

    }

    public void setStage(Stage stage){
        this.stage = stage;
        stage.setMinHeight(500);
        stage.setMinWidth(505);
        stage.setHeight(700);
        stage.setWidth(650);
    }

    public void backToHome(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sudokuosman/accueil.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) vBoxMain.getScene().getWindow();

            AccueilController controller = loader.getController();
            controller.setStage(stage);

            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




