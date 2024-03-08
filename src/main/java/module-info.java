module com.example.barkadista {
    requires javafx.controls;
    requires com.jfoenix;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires mysql.connector.j;

    opens com.example.barkadista to javafx.fxml;
    exports com.example.barkadista;
}