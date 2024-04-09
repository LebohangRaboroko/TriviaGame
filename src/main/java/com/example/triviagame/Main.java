package com.example.triviagame;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.util.Objects;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Main extends Application {

    public int questionIndex = 0;
    public int score = 0;
    public String[] imageFiles = {"1.jpg", "2.jpg", "3.jpg"};
    public String[] videoFiles = {"1.mp4", "2.mp4"};

    public String[] questions = {
            "What is Lesotho's Currency?",
            "What is the name of the mountain in the picture?",
            "What name is given to this hat?",
            "What is the name of this Basotho women traditional dance?",
            "Name of Village where Moshoeshoe I's fortress is found?"
    };
    public String[][] answers = {
            {"Dollar", "Euro", "Loti", "Rand"},
            {"Thabana-li-'Mele", "Thabana Ntlenyane", "Thaba Chitja", "Thaba Phechele"},
            {"Ts'ets'e", "Mosetla", "Mokorotlo", "Khaebana"},
            {"Ntlamu", "Mokhibo", "Seqatha-majoana", "Litolobonya"},
            {"Thaba bosiu Cultural village", "Maseru east", "Motimposo", "Menkhoaneng"}
    };
    public String[] questionAnswers = {"Loti", "Thabana-li-'Mele","Mokorotlo","Litolobonya","Thaba bosiu Cultural village"};

    private Timeline timeline;
    private int timeSeconds = 60;

    @Override
    public void start(Stage stage) {
        VBox root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(20);

        Label timerLabel = new Label("Time: " + timeSeconds + "s");

        timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1), event -> {
            timeSeconds--;
            timerLabel.setText("Time: " + timeSeconds + "s");
            if (timeSeconds <= 0) {
                timeline.stop();
                showAlert("Time's up! Quiz ended. Your final score: " + score);
            }
        }));

        timeline.playFromStart();

        Label questionHolder = new Label(questions[questionIndex]);
        questionHolder.setAlignment(Pos.CENTER);

        Pane mediaPane = new Pane();
        mediaPane.setPrefSize(400, 400);

        HBox answerButtons = new HBox(30);
        answerButtons.setAlignment(Pos.CENTER);

        Button[] buttons = new Button[4];
        for (int i = 0; i < 4; i++) {
            int index = i;
            buttons[i] = new Button(answers[questionIndex][index]);
            buttons[i].setOnAction(event -> {
                String selectedAnswer = buttons[index].getText();
                String correctAnswer = questionAnswers[questionIndex];
                if (selectedAnswer.equals(correctAnswer)) {
                    score++;
                    showAlert("Correct! Your score: " + score);
                } else {
                    showAlert("Incorrect! The correct answer is: " + correctAnswer);
                }
                questionIndex++;
                if (questionIndex < questions.length) {
                    switchAnswers(questionIndex, questionHolder, mediaPane, buttons);
                } else {
                    timeline.stop();
                    showAlert("Quiz ended. Your final score: " + score);
                }
            });
            answerButtons.getChildren().add(buttons[i]);
        }

        root.getChildren().addAll(timerLabel, questionHolder, mediaPane, answerButtons);
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        stage.setTitle("Lesotho Trivia Game");
        stage.setScene(scene);
        stage.show();

        switchMedia(questionIndex, mediaPane);
    }

    public void switchAnswers(int index, Label questionHolder, Pane mediaPane, Button[] buttons) {
        questionHolder.setText(questions[index]);
        for (int i = 0; i < 4; i++) {
            buttons[i].setText(answers[index][i]);
        }
        switchMedia(index, mediaPane);
    }

    public void switchMedia(int index, Pane mediaPane) {
        if (index < imageFiles.length) {
            ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(imageFiles[index]))));
            imageView.setFitWidth(600);
            imageView.setFitHeight(600);
            imageView.setPreserveRatio(true);

            imageView.setLayoutX((mediaPane.getWidth() - imageView.getBoundsInLocal().getWidth()) / 2);
            imageView.setLayoutY((mediaPane.getHeight() - imageView.getBoundsInLocal().getHeight()) / 2);

            mediaPane.getChildren().setAll(imageView);
        } else {
            MediaPlayer mediaPlayer;
            javafx.scene.media.MediaView mediaView;
            if (index - imageFiles.length == 0) {
                mediaPlayer = new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource(videoFiles[0])).toString()));
                mediaPlayer.setStartTime(new javafx.util.Duration(0));
                mediaPlayer.setStopTime(new javafx.util.Duration(5000));
                mediaPlayer.play();
                mediaView = new javafx.scene.media.MediaView(mediaPlayer);
            } else {
                mediaPlayer = new MediaPlayer(new Media(Objects.requireNonNull(getClass().getResource(videoFiles[1])).toString()));
                mediaPlayer.setStartTime(new javafx.util.Duration(19000));
                mediaPlayer.setStopTime(new javafx.util.Duration(24000));
                mediaView = new javafx.scene.media.MediaView(mediaPlayer);
            }
            mediaView.setFitWidth(600);
            mediaView.setFitHeight(600);
            mediaView.setPreserveRatio(true);

            Button playButton = new Button("Play");
            Button stopButton = new Button("Stop");

            playButton.setOnAction(event -> mediaPlayer.play());
            stopButton.setOnAction(event -> mediaPlayer.stop());

            VBox controls = new VBox(playButton, stopButton);
            controls.setAlignment(Pos.CENTER);
            controls.setSpacing(10);

            HBox mediaBox = new HBox(mediaView, controls);
            mediaBox.setAlignment(Pos.CENTER);
            mediaBox.setSpacing(10);

            mediaBox.layoutXProperty().bind(mediaPane.widthProperty().subtract(mediaBox.widthProperty()).divide(2));
            mediaBox.layoutYProperty().bind(mediaPane.heightProperty().subtract(mediaBox.heightProperty()).divide(2));

            mediaPane.getChildren().setAll(mediaBox);
        }
    }

    public void showAlert(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
