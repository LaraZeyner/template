package net.mmm.mc.template.util.reflections.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

/** an implementation of {@link File} for {@link ZipEntry} */
public class ZipFile implements File {
  private final ZipDir root;
  private final ZipEntry entry;

  ZipFile(final ZipDir root, ZipEntry entry) {
    this.root = root;
    this.entry = entry;
  }

  public String getName() {
    final String name = entry.getName();
    return name.substring(name.lastIndexOf('/') + 1);
  }

  public String getRelativePath() {
    return entry.getName();
  }

  public InputStream openInputStream() throws IOException {
    return root.getJarFile().getInputStream(entry);
  }

  @Override
  public String toString() {
    return root.getPath() + "!" + java.io.File.separatorChar + entry;
  }
}
