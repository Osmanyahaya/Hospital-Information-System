package yahaya.com.hospitalinformationsystem;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.Optional;

public class PatientCRUDApp extends Application {
    private TableView<Patient> table = new TableView<>();
    private TextField idField = new TextField();
    private TextField nameField = new TextField();
    private TextField surnameField = new TextField();
    private TextField addressField = new TextField();
    private TextField phoneField = new TextField();

    private Connection conn;

    @Override
    public void start(Stage primaryStage) {
        connectToDB();
        setupTable();
        loadPatients();

        VBox form = buildForm();
        HBox buttons = buildButtons();

        VBox layout = new VBox(10, table, form, buttons);
        layout.setPadding(new Insets(20));

        primaryStage.setTitle("Patient Management");
        primaryStage.setScene(new Scene(layout, 800, 500));
        primaryStage.show();
    }

    private void connectToDB() {
        conn = DBConnection.getConnection();
    }

    private void setupTable() {
        TableColumn<Patient, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Patient, String> nameCol = new TableColumn<>("Firstname");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("firstname"));

        TableColumn<Patient, String> surnameCol = new TableColumn<>("Surname");
        surnameCol.setCellValueFactory(new PropertyValueFactory<>("surname"));

        TableColumn<Patient, String> addressCol = new TableColumn<>("Address");
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        TableColumn<Patient, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        table.getColumns().addAll(idCol, nameCol, surnameCol, addressCol, phoneCol);
    }

    private VBox buildForm() {
        idField.setPromptText("ID");
        nameField.setPromptText("Firstname");
        surnameField.setPromptText("Surname");
        addressField.setPromptText("Address");
        phoneField.setPromptText("Phone");

        idField.setDisable(true);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.addRow(0, new Label("Name:"), nameField);
        form.addRow(1, new Label("Surname:"), surnameField);
        form.addRow(2, new Label("Address:"), addressField);
        form.addRow(3, new Label("Phone:"), phoneField);

        return new VBox(10, form);
    }

    private HBox buildButtons() {
        Button add = new Button("Add");
        Button update = new Button("Update");
        Button delete = new Button("Delete");
        Button clear = new Button("Clear");

        add.setOnAction(e -> addPatient());
        update.setOnAction(e -> updatePatient());
        delete.setOnAction(e -> deletePatient());
        clear.setOnAction(e -> clearForm());

        table.setOnMouseClicked(e -> populateForm());

        return new HBox(10, add, update, delete, clear);
    }

    private void loadPatients() {
        table.getItems().clear();
        String sql = "SELECT * FROM patient";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Patient p = new Patient(
                        rs.getInt("id"),
                        rs.getString("firstname"),
                        rs.getString("surname"),
                        rs.getString("address"),
                        rs.getString("phone")
                );
                table.getItems().add(p);
            }
        } catch (SQLException e) {
            showAlert("Load Error", e.getMessage());
        }
    }

    private void addPatient() {
        String sql = "INSERT INTO patient (firstname, surname, address, phone) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nameField.getText());
            ps.setString(2, surnameField.getText());
            ps.setString(3, addressField.getText());
            ps.setString(4, phoneField.getText());
            ps.executeUpdate();
            loadPatients();
            clearForm();
        } catch (SQLException e) {
            showAlert("Insert Error", e.getMessage());
        }
    }

    private void updatePatient() {
        if (idField.getText().isEmpty()) return;

        String sql = "UPDATE patient SET firstname=?, surname=?, address=?, phone=? WHERE id=?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nameField.getText());
            ps.setString(2, surnameField.getText());
            ps.setString(3, addressField.getText());
            ps.setString(4, phoneField.getText());
            ps.setInt(5, Integer.parseInt(idField.getText()));
            ps.executeUpdate();
            loadPatients();
            clearForm();
        } catch (SQLException e) {
            showAlert("Update Error", e.getMessage());
        }
    }

    private void deletePatient() {
        Patient selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete patient?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            String sql = "DELETE FROM patient WHERE id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, selected.getId());
                ps.executeUpdate();
                loadPatients();
                clearForm();
            } catch (SQLException e) {
                showAlert("Delete Error", e.getMessage());
            }
        }
    }

    private void populateForm() {
        Patient p = table.getSelectionModel().getSelectedItem();
        if (p != null) {
            idField.setText(String.valueOf(p.getId()));
            nameField.setText(p.getFirstname());
            surnameField.setText(p.getSurname());
            addressField.setText(p.getAddress());
            phoneField.setText(p.getPhone());
        }
    }

    private void clearForm() {
        idField.clear();
        nameField.clear();
        surnameField.clear();
        addressField.clear();
        phoneField.clear();
    }

    private void showAlert(String title, String message) {
        new Alert(Alert.AlertType.ERROR, message, ButtonType.OK).showAndWait();
    }
}

