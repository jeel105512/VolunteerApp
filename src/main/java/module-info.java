module com.example.volunteerapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.volunteerapp to javafx.fxml;
    exports com.example.volunteerapp;
}