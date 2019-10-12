package net.mmm.mc.template.util;

import java.sql.Connection;
import java.sql.SQLException;

import net.mmm.mc.template.Template;
import net.mmm.mc.template.storage.Const;
import net.mmm.mc.template.storage.Data;
import net.mmm.mc.template.storage.Messages;
import net.mmm.mc.template.util.reflections.Reflections;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;

/**
 * Created by Lara on 26.02.2019 for template
 */
public final class Registerer {

  public static void performRegistration() {
    registerCommands();
    registerEvents();

    if (checkErrors()) {
      Messenger.administratorMessage(Messages.CONFIG_ERROR);
      Data.setForceDisable(true);
      Template.getInstance().onDisable();
    }
  }

  private static boolean checkErrors() {
    final Connection connection = Data.getInstance().getSql().getConnection();
    try {
      return connection == null || connection.isClosed();
    } catch (SQLException ignored) {
      return true;
    }
  }

  private static void registerCommands() {
    final Reflections reflections =
        new Reflections("net.mmm.mc." + Const.PLUGIN_NAME.toLowerCase() + ".commands");
    for (Class<? extends CommandExecutor> commandClass : reflections.getSubTypesOf(CommandExecutor.class)) {
      final String name = commandClass.getSimpleName().toLowerCase();
      try {
        Template.getInstance().getCommand(name).setExecutor(commandClass.getConstructor().newInstance());
      } catch (ReflectiveOperationException ignored) {
        Messenger.administratorMessage("Command " + name + " could not loaded.");
      }
    }
  }

  private static void registerEvents() {
    final Reflections reflections =
        new Reflections("net.mmm.mc." + Const.PLUGIN_NAME.toLowerCase() + ".listener");
    for (Class<? extends Listener> listenerClass : reflections.getSubTypesOf(Listener.class)) {
      try {
        Bukkit.getPluginManager().registerEvents(listenerClass.getConstructor().newInstance(), Template.getInstance());
      } catch (ReflectiveOperationException ignored) {
        Messenger.administratorMessage("Event " + listenerClass.getName() + " could not loaded.");
      }
    }
  }
}
