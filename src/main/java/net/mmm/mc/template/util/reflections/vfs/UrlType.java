package net.mmm.mc.template.util.reflections.vfs;

import java.net.URL;

/** a matcher and factory for a url */
public interface UrlType {
  boolean matches(URL url) throws Exception;

  Dir createDir(URL url) throws Exception;
}
