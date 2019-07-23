package de.spexmc.mc.template.io.sql;

import de.spexmc.mc.template.storage.Data;

/**
 * Created by Lara on 13.01.2019 for template
 */
public class SQLManager extends PlayerStorageSQLHandler {

  public SQLManager() {
    init(connect());
  }

  @Override
  public void disconnect() {
    updateOnStop();
    super.disconnect();
  }

  public void updateOnStart() {
    final Data data = Data.getInstance();
    data.getCache().putAll(getPlayers());
  }

  private void updateOnStop() {

  }
}
