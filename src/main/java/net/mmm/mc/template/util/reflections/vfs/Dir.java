package net.mmm.mc.template.util.reflections.vfs;

/** an abstract vfs dir */
public interface Dir {
  String getPath();

  Iterable<File> getFiles();

  void close();
}
