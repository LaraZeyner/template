package de.spexmc.mc.template.util.mcutils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

/**
 * Created by Lara on 28.05.2019 for template
 */
public class PlayerScoreboard {
  private final Player player;

  public PlayerScoreboard(Player player) {
    this.player = player;
  }

  public void update() {
    if (isEmpty()) {
      createScoreboard();
    } else {
      updateScoreboard();
    }
  }

  public void show() {
    update();
  }

  public void hide() {
    player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
  }

  private void updateScoreboard() {
    final Objective objective = getObjective();
    for (final String score : objective.getScoreboard().getEntries()) {
      objective.getScoreboard().resetScores(score);
    }
    evaluateScoreboardContent();
  }

  private void evaluateScoreboardContent() {
    addScoreboardInfos("§7----------------",
        "§7Name:",
        "§7 -> §b" + player.getDisplayName(),
        "  ",
        "§7IP:",
        "§7 -> §6" +
            (Bukkit.getServer().getIp().equals("") ? "127.0.0.1" : Bukkit.getServer().getIp()) + ":" +
            Bukkit.getServer().getPort(),
        "   ",
        "§7Welt:",
        "§7 -> §6" + player.getWorld().getName());
  }

  private void addScoreboardInfos(String... infos) {
    for (int i = infos.length; i > 0; i--) {
      final String info = infos[i - 1];
      final Score score = getObjective().getScore(info);
      score.setScore(infos.length - i + 1);
    }
  }

  private void createScoreboard() {
    final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    final Objective objective = scoreboard.registerNewObjective("objective", "criteria");
    objective.setDisplayName("§5     Template     ");
    objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    player.setScoreboard(scoreboard);
    evaluateScoreboardContent();
  }

  private boolean isEmpty() {
    return getObjective() == null;
  }

  private Objective getObjective() {
    return player.getScoreboard().getObjective("objective");
  }
}