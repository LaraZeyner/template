package de.spexmc.mc.template.io.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.spexmc.mc.template.Template;
import de.spexmc.mc.template.io.FileManager;
import de.spexmc.mc.template.storage.Const;
import de.spexmc.mc.template.storage.Data;
import de.spexmc.mc.template.storage.Messages;

/**
 * Created by Lara on 20.07.2019 for template
 */
public class SQLConnector {
  private final Logger logger = Logger.getLogger(getClass().getName());
  private Connection connection;
  private final SQLData sqlData;

  SQLConnector() throws NumberFormatException {
    final Map<String, String> sqlConfig = FileManager.loadConfig(Const.SQL_CONFIG);
    final short port = Short.parseShort(Objects.requireNonNull(sqlConfig).get("port"));
    final String hostname = sqlConfig.get("host");
    final String username = sqlConfig.get("username");
    final String password = sqlConfig.get("password");
    final String database = sqlConfig.get("database");
    this.sqlData = new SQLData(hostname, port, username, password, database);
  }

  Connection connect() {
    try {
      Class.forName("com.mysql.jdbc.Driver");
      this.connection = DriverManager.getConnection(sqlData.getUrl(), sqlData.getUsername(), sqlData.getPassword());
    } catch (ClassNotFoundException | SQLException ex) {
      getLogger().log(Level.SEVERE, Messages.MYSQL_CONNECTION_FAILED, ex);
      Data.setForceDisable(true);
      Template.getInstance().onDisable();
    }
    return connection;
  }

  void init(Connection connection) {
    if (connection != null) {
      try (final PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS Template" +
          "(Spalte1 VARCHAR(8))")) {
        stmt.executeUpdate();

      } catch (SQLException ex) {
        logger.log(Level.SEVERE, Messages.MYSQL_CONNECTION_FAILED, ex);
        Data.setForceDisable(true);
        Template.getInstance().onDisable();
      }
    } else {
      getLogger().log(Level.SEVERE, Messages.MYSQL_CONNECTION_FAILED);
      Data.setForceDisable(true);
      Template.getInstance().onDisable();
    }
  }

  private boolean checkConnection() throws SQLException {
    if (sqlData.getConnection() != null) {
      return !sqlData.getConnection().isClosed();
    }
    return false;
  }

  public void disconnect() {
    try {
      if (checkConnection()) {
        sqlData.getConnection().close();
      }

    } catch (final SQLException ex) {
      getLogger().log(Level.SEVERE, Messages.MYSQL_CONNECTION_FAILED, ex);
      Template.getInstance().onDisable();
    }
  }

  public Connection getConnection() {
    return connection;
  }

  SQLData getSqlData() {
    return sqlData;
  }

  private Logger getLogger() {
    return logger;
  }
}