package net.mmm.mc.template.util.reflections.vfs;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mmm.mc.template.util.Messenger;
import net.mmm.mc.template.util.reflections.ReflectionsException;

public class UrlTypeVFS implements UrlType {
  private final static String[] REPLACE_EXTENSION = {".ear/", ".jar/", ".war/", ".sar/", ".har/", ".par/"};

  public boolean matches(URL url) {
    return "vfszip".equals(url.getProtocol()) || "vfsfile".equals(url.getProtocol());
  }

  public Dir createDir(final URL url) {
    try {
      final URL adaptedUrl = adaptURL(url);
      return new ZipDir(new JarFile(adaptedUrl.getFile()));
    } catch (Exception ignored) {
      Messenger.administratorMessage("Could not get URL");
    }
    try {
      return new ZipDir(new JarFile(url.getFile()));
    } catch (IOException ignored) {
      Messenger.administratorMessage("Could not get URL");
    }
    return null;
  }

  private URL adaptURL(URL url) throws MalformedURLException {
    if ("vfszip".equals(url.getProtocol())) {
      return replaceZipSeparators(url.getPath(), getRealFile());
    } else if ("vfsfile".equals(url.getProtocol())) {
      return new URL(url.toString().replace("vfsfile", "file"));
    } else {
      return url;
    }
  }

  private URL replaceZipSeparators(String path, Predicate<File> acceptFile)
      throws MalformedURLException {
    int pos = 0;
    while (pos != -1) {
      pos = findFirstMatchOfDeployableExtention(path, pos);

      if (pos > 0) {
        final File file = new File(path.substring(0, pos - 1));
        if (acceptFile.test(file)) {
          return replaceZipSeparatorStartingFrom(path, pos);
        }
      }
    }

    throw new ReflectionsException("Unable to identify the real zip file in path '" + path + "'.");
  }

  private int findFirstMatchOfDeployableExtention(String path, int pos) {
    final Pattern p = Pattern.compile("\\.[ejprw]ar/");
    final Matcher m = p.matcher(path);
    return m.find(pos) ? m.end() : -1;
  }

  private final Predicate<File> realFile = file -> file.exists() && file.isFile();

  private URL replaceZipSeparatorStartingFrom(String path, int pos)
      throws MalformedURLException {
    final String zipFile = path.substring(0, pos - 1);
    String zipPath = path.substring(pos);

    int numSubs = 1;
    for (String ext : REPLACE_EXTENSION) {
      while (zipPath.contains(ext)) {
        zipPath = zipPath.replace(ext, ext.substring(0, 4) + "!");
        numSubs++;
      }
    }

    final StringBuilder prefix = new StringBuilder();
    for (int i = 0; i < numSubs; i++) {
      prefix.append("zip:");
    }

    return zipPath.trim().isEmpty() ?
        new URL(prefix + "/" + zipFile) : new URL(prefix + "/" + zipFile + "!" + zipPath);
  }

  private Predicate<File> getRealFile() {
    return realFile;
  }
}