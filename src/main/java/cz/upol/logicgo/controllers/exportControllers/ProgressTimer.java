package cz.upol.logicgo.controllers.exportControllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.util.Duration;

public class ProgressTimer {
    private final Label label;
    private final ProgressBar progressBar;
    private final int[] progressCount;
    private Timeline timeline;
    private long startTime;

    public ProgressTimer(Label label, ProgressBar progressBar, int[] progressCount) {
        this.label = label;
        this.progressBar = progressBar;
        this.progressCount = progressCount;
    }

    public void start() {
        startTime = System.currentTimeMillis();

        timeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
            double progress = progressBar.getProgress();
            int done = progressCount[0];
            int total = progressCount[1];

            if (progress > 0 && progress < 1) {
                long elapsed = System.currentTimeMillis() - startTime;
                long estimatedTotal = (long) (elapsed / progress);
                long remaining = estimatedTotal - elapsed;

                long seconds = remaining / 1000;
                long minutes = seconds / 60;
                seconds = seconds % 60;

                String eta = String.format("%d:%02d", minutes, seconds);
                label.setText(String.format("%d/%d - %.0f%% - zbývá: %s",
                        done, total, progress * 100, eta));
            } else if (progress >= 1.0) {
                label.setText(String.format("%d/%d - 100%% - hotovo", done, total));
            } else {
                label.setText(String.format("%d/%d - %.0f%%", done, total, progress * 100));
            }
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    public void stop() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
        label.setText(String.format("%d/%d - 100%% - hotovo", progressCount[0], progressCount[1]));
    }
}
