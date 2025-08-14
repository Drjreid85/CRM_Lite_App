package crm.dao;

import crm.db.Db;
import crm.model.Customer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CustomerDao {

  public List<Customer> listAll() {
    String sql = "SELECT * FROM customers ORDER BY name";
    try (Connection c = Db.get();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      List<Customer> out = new ArrayList<>();
      while (rs.next()) out.add(map(rs));
      return out;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public List<Customer> search(String field, String query) {
    String col = switch (field) {
      case "Company" -> "company";
      case "Industry" -> "industry";
      default -> "name";
    };
    String sql = "SELECT * FROM customers WHERE " + col + " LIKE ? ORDER BY name";
    try (Connection c = Db.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, "%" + query + "%");
      try (ResultSet rs = ps.executeQuery()) {
        List<Customer> out = new ArrayList<>();
        while (rs.next()) out.add(map(rs));
        return out;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public Customer insert(Customer cu) {
    String sql = """
      INSERT INTO customers(name,company,industry,email,phone,next_followup)
      VALUES(?,?,?,?,?,?)
    """;
    try (Connection c = Db.get();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, cu.getName());
      ps.setString(2, cu.getCompany());
      ps.setString(3, cu.getIndustry());
      ps.setString(4, cu.getEmail());
      ps.setString(5, cu.getPhone());
      ps.setString(6, cu.getNextFollowUp() == null ? null : cu.getNextFollowUp().toString());
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) cu.setId(keys.getInt(1));
      }
      return cu;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void update(Customer cu) {
    String sql = """
      UPDATE customers SET name=?, company=?, industry=?, email=?, phone=?, next_followup=? WHERE id=?
    """;
    try (Connection c = Db.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, cu.getName());
      ps.setString(2, cu.getCompany());
      ps.setString(3, cu.getIndustry());
      ps.setString(4, cu.getEmail());
      ps.setString(5, cu.getPhone());
      ps.setString(6, cu.getNextFollowUp() == null ? null : cu.getNextFollowUp().toString());
      ps.setInt(7, cu.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  public void delete(int id) {
    try (Connection c = Db.get();
         PreparedStatement ps = c.prepareStatement("DELETE FROM customers WHERE id=?")) {
      ps.setInt(1, id);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  private Customer map(ResultSet rs) throws SQLException {
    String f = rs.getString("next_followup");
    LocalDate follow = (f == null || f.isBlank()) ? null : LocalDate.parse(f);
    return new Customer(
      rs.getInt("id"),
      rs.getString("name"),
      rs.getString("company"),
      rs.getString("industry"),
      rs.getString("email"),
      rs.getString("phone"),
      follow
    );
  }
}
