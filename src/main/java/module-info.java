module yahaya.com.hospitalinformationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens yahaya.com.hospitalinformationsystem to javafx.fxml;
    exports yahaya.com.hospitalinformationsystem;
}