package de.spexmc.mc.template.io.sql;

import java.sql.Connection;

import de.spexmc.mc.template.storage.Data;

/**
 * Created by Lara on 13.01.2019 for template
 */
class SQLData {
  private final String password, url, username;

  SQLData(String host, short port, String username, String password, String database) {
    if (!host.contains("/") || !host.contains(":")) {
      this.url = database.equals("") ? "jdbc:mysql://" + host + ":" + port :
          "jdbc:mysql://" + host + ":" + port + "/" + database;
      this.username = username;
      this.password = password;
    } else {
      throw new IllegalArgumentException("Dont use chars like / or :");
    }
  }

  //<editor-fold desc="getter and setter">
  Connection getConnection() {
    return Data.getInstance().getSql().getConnection();
  }

  String getPassword() {
    return password;
  }

  String getUrl() {
    return url;
  }

  String getUsername() {
    return username;
  }
  //</editor-fold>
}
