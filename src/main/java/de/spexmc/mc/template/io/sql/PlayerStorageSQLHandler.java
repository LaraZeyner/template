package de.spexmc.mc.template.io.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.spexmc.mc.template.storage.Const;
import de.spexmc.mc.template.storage.Data;

/**
 * Created by Lara on 26.02.2019 for template
 */
public class PlayerStorageSQLHandler extends SQLConnector {

  public Map<UUID, String> getPlayers() {
    final SQLManager sqlManager = Data.getInstance().getSql();
    final Map<UUID, String> players = new HashMap<>();
    try (final PreparedStatement statement = sqlManager.getSqlData().getConnection().
        prepareStatement("SELECT UUID, name FROM " + Const.PLAYERTABLE);
         final ResultSet resultSet = statement.executeQuery()) {
      while (resultSet.next()) {
        final String uuid = resultSet.getString(1);
        final String name = resultSet.getString(2);
        players.put(UUID.fromString(uuid), name);
      }
    } catch (final SQLException ex) {
      ex.printStackTrace();
    }
    return players;
  }
}
