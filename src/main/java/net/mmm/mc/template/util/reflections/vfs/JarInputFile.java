package net.mmm.mc.template.util.reflections.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;

public class JarInputFile implements File {
  private final ZipEntry entry;
  private final JarInputDir jarInputDir;
  private final long fromIndex;
  private final long endIndex;

  JarInputFile(ZipEntry entry, JarInputDir jarInputDir, long cursor, long nextCursor) {
    this.entry = entry;
    this.jarInputDir = jarInputDir;
    fromIndex = cursor;
    endIndex = nextCursor;
  }

  public String getName() {
    final String name = entry.getName();
    return name.substring(name.lastIndexOf('/') + 1);
  }

  public String getRelativePath() {
    return entry.getName();
  }

  public InputStream openInputStream() {
    return new InputStream() {
      @Override
      public int read() throws IOException {
        if (jarInputDir.getCursor() >= fromIndex && jarInputDir.getCursor() <= endIndex) {
          final int read = jarInputDir.getJarInputStream().read();
          jarInputDir.setCursor(jarInputDir.getCursor() + 1);
          return read;
        } else {
          return -1;
        }
      }
    };
  }
}
