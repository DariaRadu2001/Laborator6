module Laborator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.management;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;

    opens com.example.laborator6 to javafx.fxml;
    exports com.example.laborator6;

    opens Repository to java.management;
    exports Repository;
}