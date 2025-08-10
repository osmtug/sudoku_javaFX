package sudokuosman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import sudokuosman.view.AccueilController;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/logo2.png")));
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Accueil.fxml"));
        Scene scene = new Scene(loader.load());

        Image cursorImage = new Image(
                getClass().getResourceAsStream("/images/cursor.png")
        );
        ImageCursor sudokuCursor = new ImageCursor(
                cursorImage,
                0,
                0
        );

        scene.setCursor(sudokuCursor);

        AccueilController controller = loader.getController();
        controller.setStage(primaryStage);

        primaryStage.setTitle("Sudoku MVVM");
        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println(getClass().getResource("/images/heart.png"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}