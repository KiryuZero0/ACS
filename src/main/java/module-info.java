module com.example.acs {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires javafx.media;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.beans;
    requires spring.context;
    requires spring.web;
    requires spring.data.jpa;
    requires jakarta.persistence;
    requires spring.core;
    opens com.example.quoteserver to spring.core, spring.beans;
    exports com.example.quoteserver;
    opens com.example.acs1 to javafx.fxml;
    exports com.example.acs1;


}