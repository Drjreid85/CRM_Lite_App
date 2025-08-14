package crm.dao;

import crm.db.Db;
import crm.model.Interaction;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class InteractionDao {

  public List<Interaction> forCustomer(int customerId) {
    String sql = "SELECT * FROM interactions WHERE customer_id=? ORDER BY when_utc DESC";
    try (Connection c = Db.get();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, customerId);
      try (ResultSet rs = ps.executeQuery()) {
        List<Interaction> out = new ArrayList<>();
        while (rs.next()) out.add(map(rs));
        return out;
      }
    } catch (SQLException e) { throw new RuntimeException(e); }
  }

  public Interaction insert(Interaction inx) {
    String sql = "INSERT INTO interactions(customer_id, when_utc, kind, note) VALUES(?,?,?,?)";
    try (Connection c = Db.get();
         PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
      ps.setInt(1, inx.getCustomerId());
      ps.setString(2, inx.getWhenUtc().toString());
      ps.setString(3, inx.getKind());
      ps.setString(4, inx.getNote());
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) inx.setId(keys.getInt(1));
      }
      return inx;
    } catch (SQLException e) { throw new RuntimeException(e); }
  }

  private Interaction map(ResultSet rs) throws SQLException {
    return new Interaction(
      rs.getInt("id"),
      rs.getInt("customer_id"),
      Instant.parse(rs.getString("when_utc")),
      rs.getString("kind"),
      rs.getString("note")
    );
  }
}
