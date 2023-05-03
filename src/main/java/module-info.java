module com.game.dendytanks {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.myproj.dendytanks to javafx.fxml;
    exports com.myproj.dendytanks;

}