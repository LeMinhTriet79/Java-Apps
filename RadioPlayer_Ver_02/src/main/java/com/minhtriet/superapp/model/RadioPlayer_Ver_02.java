package com.minhtriet.superapp.model;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.shape.Line;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;

import java.util.LinkedHashMap;
import java.util.Map;

public class RadioPlayer_Ver_02 extends Application {
    private MediaPlayer mediaPlayer;
    private final Map<String, String> stations = new LinkedHashMap<>();
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        // Danh sách các đài radio (thêm, bớt tuỳ ý)
        stations.put("Radio Paradise", "http://stream.radioparadise.com/mp3-128");
        stations.put("BBC World Service", "http://stream.live.vc.bbcmedia.co.uk/bbc_world_service");
        stations.put("Smooth Jazz", "http://us3.internet-radio.com:8266/stream");
        stations.put("Classic FM (UK)", "http://media-ice.musicradio.com/ClassicFMMP3");
        stations.put("VOV Giao Thông (VN)", "http://113.160.234.106:8000/giaothong");

        // Thanh tiêu đề retro
        Label title = new Label("Vintage Radio Player");
        title.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 8 0 8 0; -fx-font-size: 16px; -fx-font-family: 'Segoe UI', 'Tahoma', 'Geneva', 'sans-serif'; -fx-font-weight: bold; -fx-border-color: #fff #909090 #909090 #fff; -fx-border-width: 2px; -fx-alignment: center; -fx-max-width: infinity;");
        title.setMaxWidth(Double.MAX_VALUE);

        // === Thanh thước kẻ (dial) ===
        HBox scaleBox = new HBox(0);
        scaleBox.setAlignment(Pos.CENTER_LEFT);
        scaleBox.setPadding(new Insets(10, 25, 10, 25));
        scaleBox.setPrefWidth(400);
        scaleBox.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #a0a0a0; -fx-border-width: 1 1 2 1;");

        int numStations = stations.size();
        double scaleWidth = 340;
        double markerRadius = 12;

        Pane scalePane = new Pane();
        scalePane.setPrefSize(scaleWidth, 65);

        // Vẽ vạch chia và tên đài
        int i = 0;
        for (String station : stations.keySet()) {
            double x = 20 + i * (scaleWidth - 40) / (numStations - 1);
            // Vạch
            Line tick = new Line(x, 28, x, 43);
            tick.setStrokeWidth(2);
            tick.setStyle("-fx-stroke: #303030;");
            scalePane.getChildren().add(tick);

            // Tên đài
            Label lbl = new Label(station);
            lbl.setStyle("-fx-font-size: 10px; -fx-font-family: monospace;");
            lbl.setLayoutX(x - 38);
            lbl.setLayoutY(47);
            scalePane.getChildren().add(lbl);

            i++;
        }

        // Thanh kẻ chính
        Line baseLine = new Line(20, 34, scaleWidth - 20, 34);
        baseLine.setStrokeWidth(6);
        baseLine.setStroke(Color.web("#bbbbbb"));
        scalePane.getChildren().add(baseLine);

        // Nút chọn đài (nút vặn cổ điển)
        Circle knob = new Circle(markerRadius, Color.web("#b06020"));
        knob.setStroke(Color.web("#604020"));
        knob.setStrokeWidth(3);
        knob.setCenterY(34);
        knob.setCenterX(20); // Đài đầu tiên
        scalePane.getChildren().add(knob);

        scaleBox.getChildren().add(scalePane);

        // === Nút Play/Stop và trạng thái ===
        Button playButton = new Button("Play");
        Button stopButton = new Button("Stop");
        playButton.setPrefWidth(70);
        stopButton.setPrefWidth(70);
        String buttonStyle = "-fx-background-color: linear-gradient(#f8f8f8 70%, #c0c0c0);"
                + "-fx-border-color: #fff #909090 #909090 #fff; -fx-border-width: 2px; "
                + "-fx-font-family: 'Segoe UI', 'Tahoma', 'Geneva', 'sans-serif'; -fx-font-size: 13px;";
        playButton.setStyle(buttonStyle);
        stopButton.setStyle(buttonStyle);

        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #fff #909090 #909090 #fff; -fx-border-width: 2px; -fx-padding: 4 8 4 8; -fx-font-size: 12px;");
        statusLabel.setPrefWidth(160);

        HBox buttonBox = new HBox(12, playButton, stopButton, statusLabel);
        buttonBox.setAlignment(Pos.CENTER);

        // Sự kiện kéo núm tròn
        final int totalStations = numStations;
        knob.setOnMousePressed(e -> knob.setFill(Color.DARKRED));
        knob.setOnMouseReleased(e -> knob.setFill(Color.web("#b06020")));
        knob.setOnMouseDragged(e -> {
            double mx = Math.max(20, Math.min(e.getX(), scaleWidth - 20));
            knob.setCenterX(mx);
        });
        knob.setOnMouseReleased(e -> {
            double x = knob.getCenterX();
            // Snap về đài gần nhất
            int nearest = 0;
            double minDist = Double.MAX_VALUE;
            int idx = 0;
            for (String st : stations.keySet()) {
                double px = 20 + idx * (scaleWidth - 40) / (totalStations - 1);
                if (Math.abs(x - px) < minDist) {
                    minDist = Math.abs(x - px);
                    nearest = idx;
                }
                idx++;
            }
            double finalX = 20 + nearest * (scaleWidth - 40) / (totalStations - 1);
            knob.setCenterX(finalX);
            // Chọn đúng đài và tự play
            String selectedStation = (String) stations.keySet().toArray()[nearest];
            stopPlaying();
            playStation(selectedStation);
        });

        // Nút Play/Stop thủ công
        playButton.setOnAction(e -> {
            // Xác định vị trí hiện tại của núm
            double x = knob.getCenterX();
            int nearest = 0;
            double minDist = Double.MAX_VALUE;
            int idx = 0;
            for (String st : stations.keySet()) {
                double px = 20 + idx * (scaleWidth - 40) / (totalStations - 1);
                if (Math.abs(x - px) < minDist) {
                    minDist = Math.abs(x - px);
                    nearest = idx;
                }
                idx++;
            }
            String selectedStation = (String) stations.keySet().toArray()[nearest];
            stopPlaying();
            playStation(selectedStation);
        });
        stopButton.setOnAction(e -> stopPlaying());

        VBox root = new VBox(0, title, scaleBox, buttonBox);
        VBox.setMargin(scaleBox, new Insets(15, 10, 6, 10));
        VBox.setMargin(buttonBox, new Insets(10, 10, 10, 10));
        root.setStyle("-fx-background-color: #c0c0c0; -fx-border-color: #fff #909090 #909090 #fff; -fx-border-width: 3px;");

        Scene scene = new Scene(root, 460, 230);
        primaryStage.setTitle("Vintage Radio Player");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void playStation(String station) {
        String url = stations.get(station);
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }
            Media media = new Media(url);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnReady(() -> statusLabel.setText("Playing: " + station));
            mediaPlayer.setOnError(() -> statusLabel.setText("Error loading station!"));
            mediaPlayer.play();
            statusLabel.setText("Loading: " + station);
        } catch (Exception ex) {
            statusLabel.setText("Error: " + ex.getMessage());
        }
    }

    private void stopPlaying() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            statusLabel.setText("Stopped");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
