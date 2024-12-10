package com.example.acs1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;

public class QuoteClient extends Application {
    private static final String SERVER_IP = "127.0.0.1"; // Adresa IP a serverului
    private static final int SERVER_PORT = 12345;       // Portul serverului

    // Elemente UI
    private TextArea chatArea = new TextArea();
    private TextField chatInput = new TextField();
    private Label quoteLabel = new Label("Press 'Get Quote' to fetch a random anime quote.");
    private ImageView imageView = new ImageView();
    private MediaView mediaView = new MediaView();

    // Conexiunea cu serverul
    private PrintWriter out;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Layout principal
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Zona de citate
        Button getQuoteButton = new Button("Get Quote");
        getQuoteButton.setOnAction(event -> sendRequest("GET_QUOTE"));

        imageView.setFitWidth(300);
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(true);

        VBox quoteBox = new VBox(10, quoteLabel, imageView, getQuoteButton);
        quoteBox.setPadding(new Insets(10));
        quoteBox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-alignment: center;");

        // Zona de chat
        chatArea.setEditable(false);
        chatInput.setPromptText("Enter your message...");
        chatInput.setOnAction(event -> {
            String message = chatInput.getText().trim();
            if (!message.isEmpty()) {
                sendRequest("CHAT:" + message);
                chatInput.clear();
            }
        });

        ScrollPane chatScroll = new ScrollPane(chatArea);
        chatScroll.setFitToWidth(true);

        VBox chatBox = new VBox(10, new Label("Chat:"), chatScroll, chatInput);
        chatBox.setPadding(new Insets(10));
        chatBox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-alignment: center;");

        // Zona de redare media
        Button loadEpisodesButton = new Button("Load Episodes");
        loadEpisodesButton.setOnAction(event -> sendRequest("GET_EPISODES"));

        VBox mediaBox = new VBox(10, mediaView, loadEpisodesButton);
        mediaBox.setPadding(new Insets(10));
        mediaBox.setStyle("-fx-border-color: gray; -fx-border-width: 1; -fx-alignment: center;");

        // Adăugare secțiuni în layout principal
        root.getChildren().addAll(quoteBox, chatBox, mediaBox);

        // Configurare scenă
        Scene scene = new Scene(root, 600, 800);
        primaryStage.setTitle("Anime Streaming Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Conectare la server
        connectToServer();
    }

    private void connectToServer() {
        try {
            // Conectare la server
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Ascultare mesaje de la server într-un thread separat
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    String serverMessage;
                    while ((serverMessage = in.readLine()) != null) {
                        final String message = serverMessage;
                        Platform.runLater(() -> processServerMessage(message));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            showError("Failed to connect to the server. Please check the server status.");
        }
    }

    private void processServerMessage(String message) {
        if (message.startsWith("QUOTE:")) {
            String quote = message.substring(6).trim();
            quoteLabel.setText(quote);
        } else if (message.startsWith("CHAT:")) {
            chatArea.appendText(message.substring(5).trim() + "\n");
        } else if (message.startsWith("EPISODES:")) {
            loadEpisodes(message.substring(9).trim());
        }
    }

    private void loadEpisodes(String episodeList) {
        String[] episodes = episodeList.split(";");
        if (episodes.length > 0) {
            String[] episode = episodes[0].split(",");
            if (episode.length == 2) {
                String title = episode[0];
                String filepath = episode[1];
                playMedia(filepath);
                chatArea.appendText("Now playing: " + title + "\n");
            }
        }
    }

    private void playMedia(String filepath) {
        try {
            Media media = new Media(new File(filepath).toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            mediaPlayer.play();
        } catch (Exception e) {
            chatArea.appendText("Error loading media file: " + filepath + "\n");
        }
    }

    private void sendRequest(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
