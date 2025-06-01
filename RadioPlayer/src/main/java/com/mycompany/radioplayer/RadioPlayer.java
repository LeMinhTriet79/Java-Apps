 

package com.mycompany.radioplayer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class RadioPlayer extends Application {
    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage primaryStage) {
        // Thay link radio bằng link bất kỳ bạn muốn nghe
        String radioUrl = "http://stream.radioparadise.com/mp3-128";

        Media media = new Media(radioUrl);
        mediaPlayer = new MediaPlayer(media);

        Button playButton = new Button("Nghe Radio");
        Button stopButton = new Button("Dừng lại");

        playButton.setOnAction(e -> mediaPlayer.play());
        stopButton.setOnAction(e -> mediaPlayer.stop());

        VBox vbox = new VBox(15, playButton, stopButton);
        vbox.setStyle("-fx-padding: 40px; -fx-alignment: center;");

        Scene scene = new Scene(vbox, 320, 200);
        primaryStage.setTitle("Radio JavaFX Maven");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}