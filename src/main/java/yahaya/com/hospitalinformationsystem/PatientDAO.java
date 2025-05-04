package yahaya.com.hospitalinformationsystem;

import java.sql.*;
import java.util.*;

public class PatientDAO {
    public void insertPatient(Patient patient) {
        String sql = "INSERT INTO Patient (firstname, surname, address, phone) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patient.getFirstname());
            stmt.setString(2, patient.getSurname());
            stmt.setString(3, patient.getAddress());
            stmt.setString(4, patient.getPhone());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Patient> getAllPatients() {
        List<Patient> list = new ArrayList<>();
        String sql = "SELECT * FROM Patient";
        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Patient p = new Patient();
                p.setId(rs.getInt("patient_id"));
                p.setFirstname(rs.getString("firstname"));
                p.setSurname(rs.getString("surname"));
                p.setAddress(rs.getString("address"));
                p.setPhone(rs.getString("phone"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updatePatient(Patient patient) {
        String sql = "UPDATE Patient SET firstname=?, surname=?, address=?, phone=? WHERE patient_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, patient.getFirstname());
            stmt.setString(2, patient.getSurname());
            stmt.setString(3, patient.getAddress());
            stmt.setString(4, patient.getPhone());
            stmt.setInt(5, patient.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deletePatient(int id) {
        String sql = "DELETE FROM Patient WHERE patient_id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
