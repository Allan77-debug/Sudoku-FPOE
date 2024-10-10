module com.example.sudokufpoe {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.sudokufpoe to javafx.fxml;
    exports com.example.sudokufpoe;
}