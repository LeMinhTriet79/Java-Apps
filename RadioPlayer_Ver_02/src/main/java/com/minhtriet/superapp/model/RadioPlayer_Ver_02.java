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

import java.util.LinkedHashMap;
import java.util.Map;


public class RadioPlayer_Ver_02 extends Application {
    private MediaPlayer mediaPlayer;
    private final Map<String, String> stations = new LinkedHashMap<>();
    private ComboBox<String> stationCombo;
    private Button playButton;
    private Button stopButton;
    private Label statusLabel;

    @Override
    public void start(Stage primaryStage) {
        // Danh sách các đài radio
        stations.put("Radio Paradise", "http://stream.radioparadise.com/mp3-128");
        stations.put("BBC World Service", "http://stream.live.vc.bbcmedia.co.uk/bbc_world_service");
        stations.put("Smooth Jazz", "http://us3.internet-radio.com:8266/stream");
        stations.put("Classic FM (UK)", "http://media-ice.musicradio.com/ClassicFMMP3");
        stations.put("VOV Giao Thông (VN)", "http://113.160.234.106:8000/giaothonghcm");

        // Thanh tiêu đề kiểu Windows 98
        Label title = new Label("Windows 98 Radio Player");
        title.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 8 0 8 0; -fx-font-size: 16px; -fx-font-family: 'Segoe UI', 'Tahoma', 'Geneva', 'sans-serif'; -fx-font-weight: bold; -fx-border-color: #fff #909090 #909090 #fff; -fx-border-width: 2px; -fx-alignment: center; -fx-max-width: infinity;");
        title.setMaxWidth(Double.MAX_VALUE);

        // ComboBox chọn đài
        stationCombo = new ComboBox<>();
        stationCombo.getItems().addAll(stations.keySet());
        stationCombo.getSelectionModel().selectFirst();
        stationCombo.setPrefWidth(300);
        stationCombo.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #fff #909090 #909090 #fff; -fx-border-width: 2px;");

        // Nút Play và Stop
        playButton = new Button("Play");
        stopButton = new Button("Stop");
        playButton.setPrefWidth(70);
        stopButton.setPrefWidth(70);

        // Kiểu nút Windows 98
        String buttonStyle = "-fx-background-color: linear-gradient(#f8f8f8 70%, #c0c0c0);" +
                "-fx-border-color: #fff #909090 #909090 #fff; -fx-border-width: 2px; " +
                "-fx-font-family: 'Segoe UI', 'Tahoma', 'Geneva', 'sans-serif'; -fx-font-size: 13px;";
        playButton.setStyle(buttonStyle);
        stopButton.setStyle(buttonStyle);

        // Label trạng thái
        statusLabel = new Label("Ready");
        statusLabel.setStyle("-fx-background-color: #e0e0e0; -fx-border-color: #fff #909090 #909090 #fff; -fx-border-width: 2px; -fx-padding: 4 8 4 8; -fx-font-size: 12px;");
        statusLabel.setPrefWidth(160);

        // Sự kiện
        playButton.setOnAction(e -> playSelectedStation());
        stopButton.setOnAction(e -> stopPlaying());

        // Đổi đài sẽ tự động stop và play mới
        stationCombo.setOnAction(e -> {
            stopPlaying();
            playSelectedStation();
        });

        // Layout cho nút
        HBox buttonBox = new HBox(12, playButton, stopButton, statusLabel);
        buttonBox.setAlignment(Pos.CENTER);

        // Tổng layout kiểu Windows 98
        VBox root = new VBox(0,
                title,
                new Pane(), // khoảng trắng nhỏ
                new HBox(8, new Label("Station:"), stationCombo),
                new Pane(),
                buttonBox
        );
        VBox.setMargin(root.getChildren().get(1), new Insets(10));
        VBox.setMargin(root.getChildren().get(2), new Insets(12, 12, 4, 12));
        VBox.setMargin(root.getChildren().get(3), new Insets(5));
        VBox.setMargin(root.getChildren().get(4), new Insets(12, 12, 12, 12));
        root.setStyle("-fx-background-color: #c0c0c0; -fx-border-color: #fff #909090 #909090 #fff; -fx-border-width: 3px;");

        Scene scene = new Scene(root, 430, 200);

        primaryStage.setTitle("Windows 98 Radio Player");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void playSelectedStation() {
        String station = stationCombo.getSelectionModel().getSelectedItem();
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