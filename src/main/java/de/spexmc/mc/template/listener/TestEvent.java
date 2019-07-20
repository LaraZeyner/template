package de.spexmc.mc.template.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

/**
 * Created by Lara on 26.02.2019 for template
 */
public class TestEvent implements Listener {

  @EventHandler
  public void onEvent(AsyncPlayerPreLoginEvent preLoginEvent) {
    preLoginEvent.allow();
  }
}
