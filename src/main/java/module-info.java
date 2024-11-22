module cs211.project {
    requires javafx.controls;
    requires javafx.fxml;
    requires bcrypt;
    requires com.google.gson;

    opens cs211.project.cs211661project to javafx.fxml;
    exports cs211.project.cs211661project;

    exports cs211.project.controllers;
    opens cs211.project.controllers to javafx.fxml;

    exports cs211.project.controllers.components;
    opens cs211.project.controllers.components to javafx.fxml;

    exports cs211.project.models;
    opens cs211.project.models to com.google.gson;

    exports cs211.project.models.collections;
    opens cs211.project.models.collections to com.google.gson;

    exports cs211.project.services;
    opens cs211.project.services to com.google.gson, javafx.fxml;
}