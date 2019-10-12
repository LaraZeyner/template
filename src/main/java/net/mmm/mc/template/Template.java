package net.mmm.mc.template;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.mmm.mc.template.io.sql.SQLManager;
import net.mmm.mc.template.util.Registerer;
import net.mmm.mc.template.storage.Data;
import net.mmm.mc.template.storage.Messages;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Lara on 26.02.2019 for template
 * Replace all template and Template of Classnames, Variablenames, Packagenames, etc with your projectname
 */
public class Template extends JavaPlugin {
  private static Template instance;
  private final Logger logger = Logger.getLogger(getClass().getName());

  public static Template getInstance() {
    return instance;
  }

  @Override
  public void onEnable() {
    logger.log(Level.INFO, Messages.ENABLING);
    instance = this;
    final Data data = Data.getInstance();
    data.getSql().updateOnStart();

    Registerer.performRegistration();
    logger.log(Level.INFO, Messages.SUCCESSFULLY_ENABLED);
  }

  @Override
  public void onDisable() {
    logger.log(Level.INFO, Messages.DISABLING);
    if (!Data.isForceDisable()) {
      final SQLManager sql = Data.getInstance().getSql();
      sql.disconnect();
    }
    logger.log(Level.INFO, Messages.SUCCESSFULLY_DISABLED);
  }

}