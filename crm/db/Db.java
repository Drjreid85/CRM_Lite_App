package crm.db;

import java.sql.*;

public final class Db {
  private static final String URL = "jdbc:sqlite:crm_lite.db";

  // Add this block
  static {
    try {
      Class.forName("org.sqlite.JDBC");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("SQLite JDBC driver not found on classpath", e);
    }
  }

  private Db() { }

  public static void init() {
    try (Connection c = get();
         Statement st = c.createStatement()) {

      st.executeUpdate("""
        CREATE TABLE IF NOT EXISTS customers (
          id            INTEGER PRIMARY KEY AUTOINCREMENT,
          name          TEXT NOT NULL,
          company       TEXT,
          industry      TEXT,
          email         TEXT,
          phone         TEXT,
          next_followup TEXT
        )
      """);
      st.executeUpdate("""
        CREATE TABLE IF NOT EXISTS interactions (
          id           INTEGER PRIMARY KEY AUTOINCREMENT,
          customer_id  INTEGER NOT NULL,
          when_utc     TEXT NOT NULL,
          kind         TEXT NOT NULL,
          note         TEXT,
          FOREIGN KEY(customer_id) REFERENCES customers(id) ON DELETE CASCADE
        )
      """);
    } catch (SQLException e) {
      throw new RuntimeException("DB init failed", e);
    }
  }

  public static Connection get() throws SQLException {
    return DriverManager.getConnection(URL);
  }
}
