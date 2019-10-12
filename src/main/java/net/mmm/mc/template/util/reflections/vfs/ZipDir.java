package net.mmm.mc.template.util.reflections.vfs;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.google.common.collect.AbstractIterator;
import net.mmm.mc.template.util.Messenger;

/** an implementation of {@link Dir} for {@link java.util.zip.ZipFile} */
public class ZipDir implements Dir {
  private final java.util.zip.ZipFile jarFile;

  ZipDir(JarFile jarFile) {
    this.jarFile = jarFile;
  }

  public String getPath() {
    return getJarFile().getName();
  }

  public Iterable<File> getFiles() {
    return () -> new AbstractIterator<File>() {
      Enumeration<? extends ZipEntry> getEntries() {
        return entries;
      }

      private final Enumeration<? extends ZipEntry> entries = getJarFile().entries();

      protected File computeNext() {
        while (getEntries().hasMoreElements()) {
          final ZipEntry entry = getEntries().nextElement();
          if (!entry.isDirectory()) {
            return new ZipFile(ZipDir.this, entry);
          }
        }

        return endOfData();
      }
    };
  }

  public void close() {
    try {
      getJarFile().close();
    } catch (IOException e) {
      Messenger.administratorMessage("Could not close JarFile");
    }
  }

  @Override
  public String toString() {
    return getJarFile().getName();
  }

  java.util.zip.ZipFile getJarFile() {
    return jarFile;
  }
}
