package net.mmm.mc.template.util.reflections.vfs;

import java.io.File;
import java.util.Collections;
import java.util.Objects;
import java.util.Stack;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;

public class SystemDir implements Dir {
  private final File file;

  SystemDir(File file) {
    if (file != null && (!file.isDirectory() || !file.canRead())) {
      throw new RuntimeException("cannot use dir " + file);
    }

    this.file = file;
  }

  public String getPath() {
    if (file == null) {
      return "/NO-SUCH-DIRECTORY/";
    }
    return file.getPath().replace("\\", "/");
  }

  public Iterable<net.mmm.mc.template.util.reflections.vfs.File> getFiles() {
    if (file == null || !file.exists()) {
      return Collections.emptyList();
    }
    return () -> new AbstractIterator<net.mmm.mc.template.util.reflections.vfs.File>() {
      Stack<File> getStack() {
        return stack;
      }

      private final Stack<File> stack = new Stack<>();

      {
        getStack().addAll(file.listFiles() != null ?
            Lists.newArrayList(Objects.requireNonNull(file.listFiles())) : Lists.newArrayList());
      }

      protected net.mmm.mc.template.util.reflections.vfs.File computeNext() {
        while (!getStack().isEmpty()) {
          final File file = getStack().pop();
          if (file.isDirectory()) {
            getStack().addAll(file.listFiles() != null ?
                Lists.newArrayList(Objects.requireNonNull(file.listFiles())) : Lists.newArrayList());
          } else {
            return new SystemFile(SystemDir.this, file);
          }
        }

        return endOfData();
      }
    };
  }

  public void close() {
  }

  @Override
  public String toString() {
    return getPath();
  }
}
