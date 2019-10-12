package net.mmm.mc.template.util.reflections.vfs;

import java.io.IOException;
import java.net.URL;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

import com.google.common.collect.AbstractIterator;
import net.mmm.mc.template.util.reflections.ReflectionsException;
import net.mmm.mc.template.util.reflections.util.Utils;

public class JarInputDir implements Dir {
  private final URL url;
  private JarInputStream jarInputStream;
  private long cursor, nextCursor;

  JarInputDir(URL url) {
    this.url = url;
  }

  public String getPath() {
    return url.getPath();
  }

  public Iterable<File> getFiles() {
    return () -> new AbstractIterator<File>() {

      {
        try {
          jarInputStream = new JarInputStream(url.openConnection().getInputStream());
        } catch (Exception e) {
          throw new ReflectionsException("Could not open url connection", e);
        }
      }

      protected File computeNext() {
        while (true) {
          try {
            final ZipEntry entry = jarInputStream.getNextJarEntry();
            if (entry == null) {
              return endOfData();
            }

            long size = entry.getSize();
            if (size < 0) size = 0xffffffffL + size; //JDK-6916399
            nextCursor += size;
            if (!entry.isDirectory()) {
              return new JarInputFile(entry, JarInputDir.this, cursor, nextCursor);
            }
          } catch (IOException e) {
            throw new ReflectionsException("could not get next zip entry", e);
          }
        }
      }
    };
  }

  public void close() {
    Utils.close(getJarInputStream());
  }

  JarInputStream getJarInputStream() {
    return jarInputStream;
  }

  long getCursor() {
    return cursor;
  }

  void setCursor(long cursor) {
    this.cursor = cursor;
  }
}
