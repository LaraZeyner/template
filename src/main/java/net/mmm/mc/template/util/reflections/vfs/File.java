package net.mmm.mc.template.util.reflections.vfs;

import java.io.IOException;
import java.io.InputStream;

/** an abstract vfs file */
public interface File {
  String getName();

  String getRelativePath();

  InputStream openInputStream() throws IOException;
}
