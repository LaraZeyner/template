package net.mmm.mc.template.util.reflections.vfs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarFile;

import com.google.common.collect.Lists;
import net.mmm.mc.template.util.Messenger;
import net.mmm.mc.template.util.reflections.ReflectionsException;
import net.mmm.mc.template.util.reflections.util.ClasspathHelper;

public abstract class Vfs {
  private static final List<UrlType> defaultUrlTypes = Lists.newArrayList(Vfs.DefaultUrlTypes.values());

  /** tries to create a Dir from the given url, using the defaultUrlTypes */
  public static Dir fromURL(final URL url) {
    return fromURL(url, defaultUrlTypes);
  }

  /** tries to create a Dir from the given url, using the given urlTypes */
  private static Dir fromURL(final URL url, final List<UrlType> urlTypes) {
    for (UrlType type : urlTypes) {
      try {
        if (type.matches(url)) {
          final Dir dir = type.createDir(url);
          if (dir != null) return dir;
        }
      } catch (Throwable e) {
        Messenger.administratorMessage("could not create Dir using " + type + " from url " + url.toExternalForm() + ". skipping.");
      }
    }

    throw new ReflectionsException("could not create Vfs.Dir from url, no matching UrlType was found [" + url.toExternalForm() + "]\n" +
        "either use fromURL(final URL url, final List<UrlType> urlTypes) or " +
        "use the static setDefaultURLTypes(final List<UrlType> urlTypes) or addDefaultURLTypes(UrlType urlType) " +
        "with your specialized UrlType.");
  }

  private static java.io.File getFile(URL url) {
    java.io.File file;
    String path;

    try {
      path = url.toURI().getSchemeSpecificPart();
      if ((file = new java.io.File(path)).exists()) return file;
    } catch (URISyntaxException ex) {
      Messenger.administratorMessage(ex.getMessage());
    }

    try {
      path = URLDecoder.decode(url.getPath(), "UTF-8");
      if (path.contains(".jar!")) path = path.substring(0, path.lastIndexOf(".jar!") + ".jar".length());
      if ((file = new java.io.File(path)).exists()) return file;

    } catch (UnsupportedEncodingException ex) {
      Messenger.administratorMessage(ex.getMessage());
    }

    path = url.toExternalForm();
    if (path.startsWith("jar:")) path = path.substring("jar:".length());
    if (path.startsWith("wsjar:")) path = path.substring("wsjar:".length());
    if (path.startsWith("file:")) path = path.substring("file:".length());
    if (path.contains(".jar!")) path = path.substring(0, path.indexOf(".jar!") + ".jar".length());
    if ((file = new java.io.File(path)).exists()) return file;

    path = path.replace("%20", " ");
    if ((file = new java.io.File(path)).exists()) return file;


    return null;
  }

  public enum DefaultUrlTypes implements UrlType {
    jarFile {
      public boolean matches(URL url) {
        return url.getProtocol().equals("file") && url.toExternalForm().contains(".jar");
      }

      public Dir createDir(final URL url) throws IOException {
        return new ZipDir(new JarFile(Objects.requireNonNull(getFile(url))));
      }
    },

    jarUrl {
      public boolean matches(URL url) {
        return "jar".equals(url.getProtocol()) || "zip".equals(url.getProtocol()) || "wsjar".equals(url.getProtocol());
      }

      public Dir createDir(URL url) throws Exception {
        try {
          final URLConnection urlConnection = url.openConnection();
          if (urlConnection instanceof JarURLConnection) {
            return new ZipDir(((JarURLConnection) urlConnection).getJarFile());
          }
        } catch (Throwable e) { /*fallback*/ }
        final java.io.File file = getFile(url);
        if (file != null) {
          return new ZipDir(new JarFile(file));
        }
        return null;
      }
    },

    jboss_vfs {
      public boolean matches(URL url) {
        return url.getProtocol().equals("vfs");
      }

      public Dir createDir(URL url) throws Exception {
        final Object content = url.openConnection().getContent();
        final Class<?> virtualFile = ClasspathHelper.contextClassLoader().loadClass("org.jboss.vfs.VirtualFile");
        final java.io.File physicalFile = (java.io.File) virtualFile.getMethod("getPhysicalFile").invoke(content);
        final String name = (String) virtualFile.getMethod("getName").invoke(content);
        java.io.File file = new java.io.File(physicalFile.getParentFile(), name);
        if (!file.exists() || !file.canRead()) file = physicalFile;
        return file.isDirectory() ? new SystemDir(file) : new ZipDir(new JarFile(file));
      }
    },

    jboss_vfsfile {
      public boolean matches(URL url) {
        return "vfszip".equals(url.getProtocol()) || "vfsfile".equals(url.getProtocol());
      }

      public Dir createDir(URL url) {
        return new UrlTypeVFS().createDir(url);
      }
    },

    bundle {
      public boolean matches(URL url) {
        return url.getProtocol().startsWith("bundle");
      }

      public Dir createDir(URL url) throws Exception {
        return fromURL((URL) ClasspathHelper.contextClassLoader().
            loadClass("org.eclipse.core.runtime.FileLocator").getMethod("resolve", URL.class).invoke(null, url));
      }
    },

    jarInputStream {
      public boolean matches(URL url) {
        return url.toExternalForm().contains(".jar");
      }

      public Dir createDir(final URL url) {
        return new JarInputDir(url);
      }
    }
  }
}