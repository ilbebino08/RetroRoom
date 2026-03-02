module com.retroroom {
    requires java.base;
    requires javafx.controls;
    requires javafx.fxml;

    opens com.retroroom to javafx.fxml;
    exports com.retroroom;
}
