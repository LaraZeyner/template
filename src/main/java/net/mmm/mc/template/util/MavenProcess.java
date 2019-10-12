package net.mmm.mc.template.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.StringJoiner;

import net.mmm.mc.template.storage.Const;
import net.mmm.mc.template.util.reflections.Reflections;
import org.bukkit.command.CommandExecutor;

/**
 * Created by Lara on 30.07.2019 for template
 */
public class MavenProcess {
  public void createPluginFile() throws IOException {
    final File pluginFile = new File(getResourcePath() + File.separator + "plugin.yml");

    final StringJoiner content = new StringJoiner("\n")
        .add("name: " + Const.PLUGIN_NAME)
        .add("version: " + Const.VERSION)
        .add("authors: [SpexMC-Entwicklerteam]\n")
        .add("main: net.mmm.mc." + Const.PLUGIN_NAME.toLowerCase() + "." + Const.PLUGIN_NAME + "\n")
        .add("commands: ");
    final Reflections reflections = new Reflections("net.mmm.mc." + Const.PLUGIN_NAME.toLowerCase() + ".commands");
    for (Class<? extends CommandExecutor> commandClass : reflections.getSubTypesOf(CommandExecutor.class)) {
      content.add("  " + commandClass.getSimpleName().toLowerCase() + ":");
    }

    pluginFile.createNewFile();
    try (final FileWriter writer = new FileWriter(pluginFile)) {
      writer.write(content.toString());
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  private static String getResourcePath() {
    try {
      final URI resourcePathFile = System.class.getResource("/RESOURCE_PATH").toURI();
      final String resourcePath = Files.readAllLines(Paths.get(resourcePathFile)).get(0);
      final URI rootURI = new File("").toURI();
      final URI resourceURI = new File(resourcePath).toURI();
      final URI relativeResourceURI = rootURI.relativize(resourceURI);
      return relativeResourceURI.getPath();
    } catch (URISyntaxException | IOException ex) {
      ex.printStackTrace();
    }
    return null;
  }
}