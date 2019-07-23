package de.spexmc.mc.template.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.nio.charset.Charset;

/**
 * Created by Lara on 23.07.2019 for template
 */
public final class IOUtils {
  public static void readURL(StringBuilder builder, URLConnection connection) throws IOException {
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
