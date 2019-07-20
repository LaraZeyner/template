package de.spexmc.mc.template.util.mcutils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.UUID;

import de.spexmc.mc.template.storage.Messages;
import de.spexmc.mc.template.util.Messenger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;

/**
 * Created by Lara on 31.05.2019 for template
 */
final class NameFetcher {

  /**
   * @param playerName The name of the player
   * @return The UUID of the given player
   */
  static UUID getUUID(String playerName) {
    final String url = "https://api.mojang.com/users/profiles/minecraft/" + playerName;
    try {
      final String uuidJson = callURL(url);
      if (!uuidJson.isEmpty()) {
        final JSONObject uuidObject = (JSONObject) JSONValue.parseWithException(uuidJson);
        return UUID.fromString(uuidObject.get("id").toString());
      }

    } catch (ParseException e) {
      e.printStackTrace();
    }

    return null;
  }

  /**
   * @param uuid The UUID of a player (can be trimmed or the normal version)
   * @return The name of the given player
   */
  static String getName(UUID uuid) {
    final String url = "https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names";
    try {
      final String nameJson = callURL(url);
      final JSONArray nameValue = (JSONArray) JSONValue.parseWithException(nameJson);
      final String playerSlot = nameValue.get(nameValue.size() - 1).toString();
      final JSONObject nameObject = (JSONObject) JSONValue.parseWithException(playerSlot);
      return nameObject.get("name").toString();
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return "error";
  }

  private static String callURL(String urlString) {
    final StringBuilder builder = new StringBuilder();
    try {
      final URL url = new URL(urlString);
      final URLConnection connection = url.openConnection();
      connection.setReadTimeout(60 * 1000);
      readURL(builder, connection);

    } catch (FileNotFoundException ignored) {
      if (urlString.startsWith("https://api.mojang.com/")) {
        final String playername = urlString.split("minecraft/")[1];
        Messenger.administratorMessage(Messages.PREFIX + "Der Spieler &c" + playername + "&e existiert nicht.");
      }
    } catch (IOException ex) {
      Messenger.administratorMessage(ex.getMessage());
    }

    return builder.toString();
  }

  private static void readURL(StringBuilder builder, URLConnection connection) throws IOException {
    if (connection != null && connection.getInputStream() != null) {
      try (final InputStreamReader streamReader = new InputStreamReader(connection.getInputStream(), Charset.defaultCharset());
           final BufferedReader bufferedReader = new BufferedReader(streamReader)) {
        int cp;
        while ((cp = bufferedReader.read()) != -1) {
          builder.append((char) cp);
        }
      }
    }
  }
}