module yahaya.com.hospitalinformationsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens yahaya.com.hospitalinformationsystem to javafx.fxml;
    exports yahaya.com.hospitalinformationsystem;
}