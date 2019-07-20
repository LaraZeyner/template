package de.spexmc.mc.template.util.mcutils;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Created by Lara on 28.05.2019 for template
 */
public final class ScoreboardManager {
  public static void loadScoreboard(Player player) {
    player.setScoreboard(evaluateScoreboard(player));
  }

  private static Scoreboard evaluateScoreboard(Player player) {
    //TODO (Abgie) 16.01.2019: Scoreboard konfigurieren

    return null;
  }
}
