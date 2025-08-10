package sudokuosman.view;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.util.Duration;
import sudokuosman.model.SudokuOption;
import sudokuosman.viewModel.SudokuViewModel;

import java.io.IOException;
import java.util.*;

public class SudokuController {

    public VBox vBoxMain;
    public Button btnRetour;
    public ImageView heart1;
    public ImageView heart2;
    public ImageView heart3;
    public Label timerLabel;

    private Timeline timeline;
    private int seconds = 0;
    private boolean cantPlay;

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

    private SequentialTransition victorySequence;
    private Timeline gameOverTimeline;


    public void initialize() {
        viewModel = new SudokuViewModel();
        labels = new Label[SIZE][SIZE];
        buttons = new Button[SIZE+1];
        vBoxMain.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) { setValue(0); }
            if (event.getCode() == KeyCode.SPACE) { return; }
            String key = event.getText();

            if (key.matches("[0-9]")) {
                if (selectedRow != -1 && selectedCol != -1) {
                    int val = Integer.parseInt(key);
                    setValue(val);
                }
            }

            switch (event.getCode()){
                case DOWN -> {
                    if (selectedRow + 1 < SIZE && selectedRow >= 0)
                        selectCell(selectedRow+1, selectedCol);
                }
                case UP -> {
                    if (selectedRow < SIZE && selectedRow-1 >= 0)
                        selectCell(selectedRow-1, selectedCol);
                }
                case LEFT -> {
                    if (selectedCol < SIZE && selectedCol-1 >= 0)
                        selectCell(selectedRow, selectedCol-1);
                }
                case RIGHT -> {
                    if (selectedCol + 1 < SIZE && selectedCol >= 0)
                        selectCell(selectedRow, selectedCol+1);
                }
            }
        });
        vBoxMain.setFocusTraversable(true);

        Platform.runLater(() -> vBoxMain.requestFocus());

        switchContainer.setOnMouseClicked(e -> toggleSwitch());

        btnRetour.setOnMouseClicked(event -> backToHome());

        vBoxMain.setOnMouseClicked(event -> selectCell(-1, -1));

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

        cantPlay = false;

        startTimer();
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

        });
        return btn;
    }

    private void setStyle() {

        normalLabel.setStyle("-fx-text-fill: "+ SudokuOption.getNumberColor() +";");
        annotationLabel.setStyle("-fx-text-fill: "+ SudokuOption.getNumberColor() +";");
        timerLabel.setStyle("-fx-text-fill: "+ SudokuOption.getNumberColor() +"; -fx-font-weight: bold; -fx-font-size: 18;");

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
        for (int[] value : selectedZone) {
            int row = value[0];
            int col = value[1];

            labels[row][col].setStyle(
                    "-fx-background-color: " + SudokuOption.getselectedZoneColor() + ";" +
                            "-fx-text-fill: " + getNumberColor(row, col) + ";" +
                            "-fx-font-weight: bold;" +
                            " -fx-alignment: center;" +
                            "-fx-font-size: 18;" +
                            getBorderThickness(row, col) +
                            "-fx-border-color: " + SudokuOption.getNumberColor() + ";" +
                            "-fx-border-radius: 5px;" +
                            "-fx-background-radius: 5px;"
            );
        }

        List<int[]> sameNumber = viewModel.getSameNumber(selectedRow, selectedCol);
        for (int[] ints : sameNumber) {
            int row = ints[0];
            int col = ints[1];
            labels[row][col].setStyle(
                    "-fx-background-color: " + SudokuOption.getSameNumberColor() + ";" +
                            "-fx-text-fill: " + getNumberColor(row, col) + ";" +
                            "-fx-font-weight: bold;" +
                            " -fx-alignment: center;" +
                            "-fx-font-size: 18;" +
                            getBorderThickness(row, col) +
                            "-fx-border-color: " + SudokuOption.getNumberColor() + ";" +
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
        return switch (viewModel.getNumberColor(row, col)) {
            case 1 -> "blue";
            case -1 -> "red";
            default -> SudokuOption.getNumberColor().toString();
        };
    }

    private void setValue(int val){
        if (cantPlay || !selectedCellIsInGrid()) { return; }
        int res = viewModel.setValueIsCorrect(selectedRow, selectedCol, val);
        if ( res == 0){
            viewModel.decrementHealth();
            if (viewModel.getHealth() == 2){
                shakeAllLabels(700, 4);
            }else if (viewModel.getHealth() == 1){
                shakeAllLabels(700, 8);
            }else{
                cantPlay = true;
                stopTimer();
                gameOverAnimation();
            }

            updateHearts(viewModel.getHealth());
        }else if (res == 1){
            shockwaveFrom(selectedRow, selectedCol);
        }
        if (viewModel.numberIsComplete(val)){
            buttons[val].setDisable(true);
        }

        if (viewModel.isFinished()){
            cantPlay = true;
            playVictorySequence();
        }

        setStyle();
    }

    private boolean selectedCellIsInGrid() {
        return selectedRow < SIZE && selectedRow >= 0 && selectedCol < SIZE && selectedCol >= 0;
    }


    public void updateHearts(int nbCoeurs) {
        // Clamp the value to [0, 3]
        nbCoeurs = Math.max(0, Math.min(3, nbCoeurs));

        // Load images
        Image fullHeart = new Image(Objects.requireNonNull(getClass().getResource("/images/heart.png")).toExternalForm());
        Image emptyHeart = new Image(Objects.requireNonNull(getClass().getResource("/images/heart_empty.png")).toExternalForm());

        // Update each heart
        heart1.setImage(nbCoeurs >= 1 ? fullHeart : emptyHeart);
        heart2.setImage(nbCoeurs >= 2 ? fullHeart : emptyHeart);
        heart3.setImage(nbCoeurs == 3 ? fullHeart : emptyHeart);
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
        stage.setMinHeight(500);
        stage.setMinWidth(505);
        stage.setHeight(700);
        stage.setWidth(650);
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public void backToHome(){
        try {
            Stage stage = (Stage) vBoxMain.getScene().getWindow();
            if (stage != null){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/sudokuosman/accueil.fxml"));
                Parent root = loader.load();

                AccueilController controller = loader.getController();
                stopAllAnimations();
                controller.setStage(stage);

                Scene scene = new Scene(root);

                Image cursorImage = new Image(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/cursor.png"))
                );
                ImageCursor sudokuCursor = new ImageCursor(
                        cursorImage,
                        0,
                        0
                );

                scene.setCursor(sudokuCursor);
                stage.setScene(scene);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopAllAnimations() {
        if (victorySequence != null) {
            victorySequence.stop();
        }

        if (gameOverTimeline != null) {
            gameOverTimeline.stop();
        }

        // Ajoute ici toute autre animation potentielle
    }

    public void startTimer() {
        if (timeline != null) {
            timeline.stop(); // reset previous if any
        }

        seconds = 0;
        timerLabel.setText("00:00");

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            seconds++;
            int minutes = seconds / 60;
            int sec = seconds % 60;
            timerLabel.setText(String.format("%02d:%02d", minutes, sec));
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void stopTimer() {
        if (timeline != null) {
            timeline.stop();
        }
    }

    public void shakeAllLabels(int durationMillis, double initialIntensity) {
        Random rand = new Random();
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        final int frameInterval = 30; // ms
        final int totalFrames = durationMillis / frameInterval;

        final int[] currentFrame = {0};

        KeyFrame frame = new KeyFrame(Duration.millis(frameInterval), e -> {
            double progress = (double) currentFrame[0] / totalFrames;
            double intensity = initialIntensity * (1.0 - progress); // diminue progressivement

            for (Label[] value : labels) {
                for (Label label : value) {
                    if (label == null) continue;

                    double dx = (rand.nextDouble() * 2 - 1) * intensity;
                    double dy = (rand.nextDouble() * 2 - 1) * intensity;
                    double angle = (rand.nextDouble() * 2 - 1) * intensity;

                    label.setTranslateX(dx);
                    label.setTranslateY(dy);
                    label.setRotate(angle);
                }
            }

            currentFrame[0]++;
            if (currentFrame[0] >= totalFrames) {
                timeline.stop();
                // Remise en position normale
                for (Label[] value : labels) {
                    for (Label label : value) {
                        if (label != null) {
                            label.setTranslateX(0);
                            label.setTranslateY(0);
                            label.setRotate(0);
                        }
                    }
                }
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    public void gameOverAnimation() {
        Random rand = new Random();
        double centerX = gridPane.getWidth() / 2;
        double centerY = gridPane.getHeight() / 2;

        int durationMillis = 1500;
        int frameInterval = 30;
        int totalFrames = durationMillis / frameInterval;
        double maxIntensity = 8.0;

        int[] currentFrame = {0};

        gameOverTimeline = new Timeline();
        gameOverTimeline.setCycleCount(Timeline.INDEFINITE);

        KeyFrame trembleFrame = new KeyFrame(Duration.millis(frameInterval), e -> {
            double progress = (double) currentFrame[0] / totalFrames;
            double intensity = maxIntensity * (1 - progress);

            for (Label[] value : labels) {
                for (Label label : value) {
                    if (label == null) continue;

                    label.textProperty().unbind();
                    label.setText(randomChar());

                    double dx = (rand.nextDouble() * 2 - 1) * intensity;
                    double dy = (rand.nextDouble() * 2 - 1) * intensity;
                    double angle = (rand.nextDouble() * 2 - 1) * intensity;

                    label.setTranslateX(dx);
                    label.setTranslateY(dy);
                    label.setRotate(angle);
                }
            }

            currentFrame[0]++;
            if (currentFrame[0] >= totalFrames) {
                gameOverTimeline.stop();
                moveToCenterWithTremble(rand, centerX, centerY);
            }
        });

        gameOverTimeline.getKeyFrames().add(trembleFrame);
        gameOverTimeline.play();
    }

    private void moveToCenterWithTremble(Random rand, double centerX, double centerY) {
        int durationMillis = 2000;
        int frameInterval = 30;
        int totalFrames = durationMillis / frameInterval;
        int[] currentFrame = {0};
        double maxTrembleIntensity = 8.0;

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(frameInterval), e -> {
            double progress = (double) currentFrame[0] / totalFrames;
            double intensity = maxTrembleIntensity * progress+4; // Tremblement augmente

            for (Label[] value : labels) {
                for (Label label : value) {
                    if (label == null) continue;

                    label.textProperty().unbind();

                    double labelCenterX = label.getLayoutX() + label.getWidth() / 2;
                    double labelCenterY = label.getLayoutY() + label.getHeight() / 2;

                    label.setText(randomChar());

                    double diffX = centerX - labelCenterX;
                    double diffY = centerY - labelCenterY;

                    double moveX = diffX * (progress * 0.5);
                    double moveY = diffY * (progress * 0.5);

                    double dx = (rand.nextDouble() * 2 - 1) * intensity;
                    double dy = (rand.nextDouble() * 2 - 1) * intensity;
                    double angle = (rand.nextDouble() * 2 - 1) * intensity;

                    label.setTranslateX(moveX + dx);
                    label.setTranslateY(moveY + dy);
                    label.setRotate(angle);
                }
            }

            currentFrame[0]++;
            if (currentFrame[0] >= totalFrames) {
                timeline.stop();
                explosionWithGravity(rand);
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }


    private void explosionWithGravity(Random rand) {
        int frameInterval = 30;

        // Pour stocker la vitesse initiale de chaque label (dx, dy, rotationSpeed)
        class Velocity {
            final double vx;
            double vy;
            final double vr;
            Velocity(double vx, double vy, double vr) {
                this.vx = vx;
                this.vy = vy;
                this.vr = vr;
            }
        }

        Velocity[][] velocities = new Velocity[labels.length][labels[0].length];
        double gravity = 0.5; // accélération vers le bas

        // Initialiser vitesses aléatoires de départ (explosion)
        for (int row = 0; row < labels.length; row++) {
            for (int col = 0; col < labels[row].length; col++) {
                Label label = labels[row][col];
                if (label == null) continue;

                double angle = rand.nextDouble() * 2 * Math.PI;
                double speed = 12 + rand.nextDouble() * 4; // vitesse initiale entre 8 et 12 px/frame
                double vx = Math.cos(angle) * speed;
                double vy = Math.sin(angle) * speed;
                double vr = (rand.nextDouble() * 10 - 5); // vitesse rotation

                velocities[row][col] = new Velocity(vx, vy, vr);
            }
        }

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        KeyFrame frame = new KeyFrame(Duration.millis(frameInterval), e -> {
            boolean allOut = true; // pour détecter si tous les labels sont hors écran

            for (int row = 0; row < labels.length; row++) {
                for (int col = 0; col < labels[row].length; col++) {
                    Label label = labels[row][col];
                    if (label == null) continue;

                    Velocity v = velocities[row][col];
                    if (v == null) continue;

                    // Mise à jour position et rotation
                    label.setTranslateX(label.getTranslateX() + v.vx);
                    label.setTranslateY(label.getTranslateY() + v.vy);
                    label.setRotate(label.getRotate() + v.vr);

                    // Applique gravité
                    v.vy += gravity;

                    // Récupérer la position du label dans la scène
                    Point2D scenePos = label.localToScene(0, 0);

                    boolean visible = isVisible(scenePos, label);

                    if (visible) {
                        allOut = false;  // Au moins un label est encore visible
                    }
                }
            }

            // Si tous les labels sont sortis, stoppe l’animation
            if (allOut) {
                timeline.stop();
                backToHome();
            }
        });

        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    private static boolean isVisible(Point2D scenePos, Label label) {
        double x = scenePos.getX();
        double y = scenePos.getY();

        // Récupérer la taille de la fenêtre (ou de ton conteneur visible)
        double sceneWidth = label.getScene().getWidth();
        double sceneHeight = label.getScene().getHeight();

        // Vérifier si le label est toujours visible dans la scène (avec un margin)
        return x >= -label.getWidth() && x <= sceneWidth + label.getWidth() &&
                y >= -label.getHeight() && y <= sceneHeight + label.getHeight();
    }


    private String randomChar() {
        String chars = "!@#$%&*+?01░▒▓█";
        Random rand = new Random();
        return String.valueOf(chars.charAt(rand.nextInt(chars.length())));
    }

    public void shockwaveFrom(int originRow, int originCol) {
        int maxRadius = Math.max(labels.length, labels[0].length);
        int delayBetweenRings = 80; // ms entre chaque cercle

        for (int radius = 0; radius <= maxRadius; radius++) {
            int currentRadius = radius;

            PauseTransition delay = new PauseTransition(Duration.millis(radius * delayBetweenRings));
            delay.setOnFinished(e -> {
                for (int row = 0; row < labels.length; row++) {
                    for (int col = 0; col < labels[0].length; col++) {
                        if (labels[row][col] == null) continue;

                        int dist = Math.abs(row - originRow) + Math.abs(col - originCol);
                        if (dist == currentRadius) {
                            //playShockTremble(labels[row][col]);
                            playPulseEffect(labels[row][col]);
                        }
                    }
                }
            });
            delay.play();
        }
    }

    private void playPulseEffect(Label label) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), label);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), label);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        SequentialTransition pulse = new SequentialTransition(scaleUp, scaleDown);
        pulse.play();
    }

    public void illuminateSpiralGrid() {
        int rows = labels.length;
        int cols = labels[0].length;
        List<Label> spiralOrder = new ArrayList<>();

        int top = 0, bottom = rows - 1, left = 0, right = cols - 1;

        while (top <= bottom && left <= right) {
            spiralOrder.addAll(Arrays.asList(labels[top]).subList(left, right + 1));
            top++;
            for (int i = top; i <= bottom; i++) spiralOrder.add(labels[i][right]);
            right--;
            if (top <= bottom)
                for (int j = right; j >= left; j--) spiralOrder.add(labels[bottom][j]);
            bottom--;
            if (left <= right)
                for (int i = bottom; i >= top; i--) spiralOrder.add(labels[i][left]);
            left++;
        }

        for (int i = 0; i < spiralOrder.size(); i++) {
            Label label = spiralOrder.get(i);
            if (label == null) continue;

            Timeline t = new Timeline(
                    new KeyFrame(Duration.ZERO, new KeyValue(label.scaleXProperty(), 1), new KeyValue(label.scaleYProperty(), 1)),
                    new KeyFrame(Duration.millis(100), new KeyValue(label.scaleXProperty(), 1.3), new KeyValue(label.scaleYProperty(), 1.3)),
                    new KeyFrame(Duration.millis(200), new KeyValue(label.scaleXProperty(), 1), new KeyValue(label.scaleYProperty(), 1))
            );
            t.setDelay(Duration.millis(i * 40));
            t.play();
        }
    }

    public void animateRainbowCycle() {
        Color[] rainbow = {
                Color.RED, Color.ORANGE, Color.YELLOW,
                Color.LIMEGREEN, Color.CYAN, Color.INDIGO, Color.MAGENTA
        };

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Animation.INDEFINITE);

        for (int row = 0; row < labels.length; row++) {
            for (int col = 0; col < labels[0].length; col++) {
                Label label = labels[row][col];
                if (label == null) continue;

                int delay = (row + col) * 100;
                KeyFrame kf = new KeyFrame(Duration.millis(delay), evt -> {
                    int index = (int) ((System.currentTimeMillis() / 100) % rainbow.length);
                    label.setTextFill(rainbow[index]);
                });
                timeline.getKeyFrames().add(kf);
            }
        }

        timeline.play();
    }

    public void centralPulseWave() {
        int rows = labels.length;
        int cols = labels[0].length;
        int centerRow = rows / 2;
        int centerCol = cols / 2;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Label label = labels[row][col];
                if (label == null) continue;

                double dist = Math.hypot(row - centerRow, col - centerCol);
                int delay = (int) (dist * 80);

                Timeline wave = new Timeline(
                        new KeyFrame(Duration.ZERO,
                                new KeyValue(label.opacityProperty(), 1),
                                new KeyValue(label.scaleXProperty(), 1),
                                new KeyValue(label.scaleYProperty(), 1)
                        ),
                        new KeyFrame(Duration.millis(300),
                                new KeyValue(label.opacityProperty(), 0.4),
                                new KeyValue(label.scaleXProperty(), 1.4),
                                new KeyValue(label.scaleYProperty(), 1.4)
                        ),
                        new KeyFrame(Duration.millis(600),
                                new KeyValue(label.opacityProperty(), 1),
                                new KeyValue(label.scaleXProperty(), 1),
                                new KeyValue(label.scaleYProperty(), 1)
                        )
                );
                wave.setDelay(Duration.millis(delay));
                wave.play();
            }
        }
    }

    public void explodeConfettiWave() {
        Pane overlay = new Pane();
        gridPane.getChildren().add(overlay);
        overlay.setMouseTransparent(true);

        Random rand = new Random();

        for (Label[] rowLabels : labels) {
            for (Label label : rowLabels) {
                if (label == null) continue;

                Bounds bounds = label.localToScene(label.getBoundsInLocal());
                double centerX = bounds.getMinX() + bounds.getWidth() / 2;
                double centerY = bounds.getMinY() + bounds.getHeight() / 2;

                for (int i = 0; i < 10; i++) {
                    Circle confetti = new Circle(1.5, Color.hsb(rand.nextDouble() * 360, 1.0, 1.0));
                    confetti.setTranslateX(centerX);
                    confetti.setTranslateY(centerY);
                    overlay.getChildren().add(confetti);

                    double angle = rand.nextDouble() * 2 * Math.PI;
                    double distance = 80 + rand.nextDouble() * 40;
                    double dx = Math.cos(angle) * distance;
                    double dy = Math.sin(angle) * distance;

                    TranslateTransition move = new TranslateTransition(Duration.millis(1200), confetti);
                    move.setByX(dx);
                    move.setByY(dy);

                    FadeTransition fade = new FadeTransition(Duration.millis(1000), confetti);
                    fade.setToValue(0);

                    ParallelTransition p = new ParallelTransition(move, fade);
                    p.setOnFinished(e -> overlay.getChildren().remove(confetti));
                    p.play();
                }
            }
        }
    }

    public void finalAscension() {
        for (Label[] rowLabels : labels) {
            for (Label label : rowLabels) {
                if (label == null) continue;

                TranslateTransition rise = new TranslateTransition(Duration.seconds(2), label);
                rise.setByY(-300);
                FadeTransition fade = new FadeTransition(Duration.seconds(2), label);
                fade.setToValue(0);
                ParallelTransition p = new ParallelTransition(rise, fade);
                p.setOnFinished(e -> label.setVisible(false));
                p.play();
            }
        }
    }


    public void playVictorySequence() {
        animateRainbowCycle();

        PauseTransition pause1 = new PauseTransition(Duration.seconds(0.5));
        pause1.setOnFinished(e -> illuminateSpiralGrid());

        PauseTransition pause2 = new PauseTransition(Duration.seconds(3.2));
        pause2.setOnFinished(e -> centralPulseWave());

        PauseTransition pause3 = new PauseTransition(Duration.seconds(1));
        pause3.setOnFinished(e -> explodeConfettiWave());

        PauseTransition pause4 = new PauseTransition(Duration.seconds(1));
        pause4.setOnFinished(e -> finalAscension());

        PauseTransition pause5 = new PauseTransition(Duration.seconds(2));
        pause5.setOnFinished(e -> backToHome());

        victorySequence  = new SequentialTransition(pause1, pause2, pause3, pause4, pause5);
        victorySequence .play();
    }

}




