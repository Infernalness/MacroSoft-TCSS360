module MacroSoft.TCSS360 {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.json;
    requires org.apache.commons.io;

    opens main.GUI to javafx.fxml;
    exports archive;
    opens archive to javafx.fxml;
    //exports main;
    exports main.data;
    opens main.data to javafx.fxml;
    exports main.GUI;
    //opens main.GUI to javafx.fxml;
}