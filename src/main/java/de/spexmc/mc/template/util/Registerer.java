package de.spexmc.mc.template.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import de.spexmc.mc.template.Template;
import de.spexmc.mc.template.commands.TestCommand;
import de.spexmc.mc.template.listener.TestEvent;
import de.spexmc.mc.template.storage.Data;
import de.spexmc.mc.template.storage.Messages;
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

  private static void registerEvents() {
    // Insert Events here
    final List<Listener> listeners = Arrays.asList(new TestEvent());
    for (Listener listener : listeners) {
      Bukkit.getPluginManager().registerEvents(listener, Template.getInstance());
    }
  }

  private static void registerCommands() {
    // Insert Commands here
    final List<CommandExecutor> commands = Arrays.asList(new TestCommand());
    for (CommandExecutor commandExecutor : commands) {
      final Class<? extends CommandExecutor> commandExecutorClass = commandExecutor.getClass();
      final String commandName = commandExecutorClass.getSimpleName().toLowerCase();
      Template.getInstance().getCommand(commandName).setExecutor(commandExecutor);
    }
  }

  /*private static void registerCommands() {
    final Reflections reflections = new Reflections("de.spexmc.mc.template.commands");
    for (Class<? extends CommandExecutor> commandClass : reflections.getSubTypesOf(CommandExecutor.class)) {
      final String name = commandClass.getSimpleName().toLowerCase();
      try {
        Varo.getInstance().getCommand(name).setExecutor(commandClass.newInstance());
      } catch (ReflectiveOperationException ignored) {
        logger.warning("Command " + name + " could not loaded.");
      }
    }
  }

  private static void registerEvents() {
    final Reflections reflections = new Reflections("de.spexmc.mc.template.listener");
    for (Class<? extends Listener> listenerClass : reflections.getSubTypesOf(Listener.class)) {
      try {
        Bukkit.getPluginManager().registerEvents(listenerClass.newInstance(), Varo.getInstance());
      } catch (ReflectiveOperationException ignored) {
        logger.warning("Event " + listenerClass.getName() + " could not loaded.");
      }
    }
  }*/
}
