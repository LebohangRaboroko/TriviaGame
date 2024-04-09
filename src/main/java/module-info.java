module com.example.triviagame {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;


    opens com.example.triviagame to javafx.fxml;
    exports com.example.triviagame;
}