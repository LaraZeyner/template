package de.spexmc.mc.template.io.sql;

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
  }

  private void updateOnStop() {

  }
}
