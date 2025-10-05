package sudokuosman.view;

import javafx.animation.*;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Objects;
import java.util.Random;

public class FireworksManager {

    private final Pane overlay;
    private final Random random = new Random();
    private final Image rocketImage = new Image(
            Objects.requireNonNull(getClass().getResourceAsStream("/images/point_blanc_transparent.png"))
    );

    private final Image ParticleImage = new Image(
            Objects.requireNonNull(getClass().getResourceAsStream("/images/point_blanc_transparent_petit.png"))
    );

    public FireworksManager(Pane overlay) {
        this.overlay = overlay;
        overlay.setMouseTransparent(true);
    }

    public void launchFirework(double startX, double groundY) {
        Firework firework = new Firework(startX, groundY, overlay);
        firework.launch();
    }

    // ---------------- Firework ----------------
    private class Firework {
        private final double startX;
        private final double groundY;
        private final ImageView rocket;
        private final Pane parent;
        private final Color color;

        Firework(double startX, double groundY, Pane parent) {
            this.startX = startX;
            this.groundY = groundY;
            this.parent = parent;

            // couleur aléatoire
            this.color = Color.hsb(random.nextDouble() * 360, 1.0, 1.0);

            this.rocket = new ImageView(rocketImage);
            double size = 400 + random.nextDouble() * 100;
            rocket.setFitWidth(size);
            rocket.setFitHeight(size);

            this.rocket.setTranslateX(startX - size / 2);
            this.rocket.setTranslateY(groundY - size / 2);

            applyColor(rocket, color);
        }

        void launch() {
            parent.getChildren().add(rocket);

            double sceneHeight = parent.getScene().getHeight();
            double peakY = sceneHeight * (0.2 + random.nextDouble() * 0.3);
            double curveX = startX + (random.nextDouble() - 0.5) * 200;

            double durationSeconds = 1.0 + random.nextDouble() * 2.0;

            PathTransition path = Animations.createCurvedPath(rocket, startX, groundY, curveX, peakY);
            path.setDuration(Duration.seconds(durationSeconds));
            path.setOnFinished(e -> explode());
            path.play();
        }

        private void explode() {
            parent.getChildren().remove(rocket);
            double centerX = rocket.getTranslateX() + rocket.getFitWidth() / 2;
            double centerY = rocket.getTranslateY() + rocket.getFitHeight() / 2;
            Explosion explosion = new Explosion(centerX, centerY, parent, color);
            explosion.play();
        }
    }

    // ---------------- Explosion ----------------
    private class Explosion {
        private final double x, y;
        private final Pane parent;
        private final Color color;

        Explosion(double x, double y, Pane parent, Color color) {
            this.x = x;
            this.y = y;
            this.parent = parent;
            this.color = color;
        }

        void play() {
            int particles = 10 + random.nextInt(10);
            for (int i = 0; i < particles; i++) {
                Particle p = new Particle(x, y, parent, color);
                p.launch();
            }
        }
    }

    // ---------------- Particle ----------------
    private class Particle {
        private final ImageView node;
        private final Pane parent;

        Particle(double x, double y, Pane parent, Color color) {
            this.parent = parent;
            this.node = new ImageView(ParticleImage);
            double size = 60 + random.nextDouble() * 30;
            node.setFitWidth(size);
            node.setFitHeight(size);

            node.setTranslateX(x - node.getFitWidth() / 2);
            node.setTranslateY(y - node.getFitHeight() / 2);

            applyColor(node, color);
        }

        void launch() {
            parent.getChildren().add(node);

            double angle = random.nextDouble() * 2 * Math.PI;
            double distance = 80 + random.nextDouble() * 120;
            double dx = Math.cos(angle) * distance;
            double dy = Math.sin(angle) * distance;

            TranslateTransition move = new TranslateTransition(Duration.millis(1500), node);
            move.setByX(dx);
            move.setByY(dy);
            move.setInterpolator(Interpolator.EASE_OUT);

            FadeTransition fade = new FadeTransition(Duration.millis(1800), node);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);

            ParallelTransition p = new ParallelTransition(move, fade);
            p.setOnFinished(e -> parent.getChildren().remove(node));
            p.play();
        }
    }

    // ---------------- Helper ----------------
    private static class Animations {
        static PathTransition createCurvedPath(ImageView node, double x0, double y0, double x1, double y1) {
            javafx.scene.shape.Path path = new javafx.scene.shape.Path();
            path.getElements().add(new javafx.scene.shape.MoveTo(x0, y0));
            path.getElements().add(new javafx.scene.shape.QuadCurveTo(
                    (x0 + x1) / 2, y0 - 100,
                    x1, y1
            ));
            PathTransition pt = new PathTransition(Duration.seconds(2), path, node);
            pt.setInterpolator(Interpolator.EASE_OUT);
            return pt;
        }
    }

    // ---------------- Utility ----------------
    private void applyColor(ImageView node, Color color) {
        Blend blend = new Blend();
        blend.setMode(BlendMode.SRC_ATOP);
        ColorInput colorInput = new ColorInput(
                0, 0,
                node.getFitWidth(),
                node.getFitHeight(),
                color
        );
        blend.setTopInput(colorInput);
        node.setEffect(blend);
    }
}
