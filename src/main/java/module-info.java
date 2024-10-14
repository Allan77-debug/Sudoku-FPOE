module com.example.sudokufpoe {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.sudokufpoe.Controller to javafx.fxml;
    exports com.example.sudokufpoe;
    exports com.example.sudokufpoe.Controller;
}
