package net.mmm.mc.template.util.reflections.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Sets;
import net.mmm.mc.template.util.Messenger;
import net.mmm.mc.template.util.reflections.Reflections;

public abstract class ClasspathHelper {

  public static ClassLoader contextClassLoader() {
    return Thread.currentThread().getContextClassLoader();
  }

  private static ClassLoader staticClassLoader() {
    return Reflections.class.getClassLoader();
  }

  public static ClassLoader[] classLoaders(ClassLoader... classLoaders) {
    if (classLoaders != null && classLoaders.length != 0) {
      return classLoaders;
    } else {
      final ClassLoader contextClassLoader = contextClassLoader();
      final ClassLoader staticClassLoader = staticClassLoader();
      return contextClassLoader != null ?
          staticClassLoader != null && contextClassLoader != staticClassLoader ?
              new ClassLoader[]{contextClassLoader, staticClassLoader} :
              new ClassLoader[]{contextClassLoader} :
          new ClassLoader[]{};

    }
  }

  static Collection<URL> forPackage(String name, ClassLoader... classLoaders) {
    return forResource(resourceName(name), classLoaders);
  }

  private static Collection<URL> forResource(String resourceName, ClassLoader... classLoaders) {
    final List<URL> result = new ArrayList<>();
    final ClassLoader[] loaders = classLoaders(classLoaders);
    for (ClassLoader classLoader : loaders) {
      try {
        final Enumeration<URL> urls = classLoader.getResources(resourceName);
        while (urls.hasMoreElements()) {
          final URL url = urls.nextElement();
          final int index = url.toExternalForm().lastIndexOf(resourceName);
          if (index != -1) {
            result.add(new URL(url.toExternalForm().substring(0, index)));
          } else {
            result.add(url);
          }
        }
      } catch (IOException ignored) {
        Messenger.administratorMessage("error getting resources");
      }
    }
    return distinctUrls(result);
  }

  static URL forClass(Class<?> aClass, ClassLoader... classLoaders) {
    final ClassLoader[] loaders = classLoaders(classLoaders);
    final String resourceName = aClass.getName().replace(".", "/") + ".class";
    for (ClassLoader classLoader : loaders) {
      try {
        final URL url = classLoader.getResource(resourceName);
        if (url != null) {
          final String normalizedUrl = url.toExternalForm().substring(0, url.toExternalForm().lastIndexOf(aClass.getPackage().getName().replace(".", "/")));
          return new URL(normalizedUrl);
        }
      } catch (MalformedURLException e) {
        Messenger.administratorMessage("Could not get URL");
      }
    }
    return null;
  }

  static Collection<URL> forClassLoader() {
    return forClassLoader(classLoaders());
  }

  static Collection<URL> forClassLoader(ClassLoader... classLoaders) {
    final Collection<URL> result = new ArrayList<>();
    final ClassLoader[] loaders = classLoaders(classLoaders);
    for (ClassLoader classLoader : loaders) {
      while (classLoader != null) {
        if (classLoader instanceof URLClassLoader) {
          final URL[] urls = ((URLClassLoader) classLoader).getURLs();
          if (urls != null) {
            result.addAll(Sets.newHashSet(urls));
          }
        }
        classLoader = classLoader.getParent();
      }
    }
    return distinctUrls(result);
  }

  private static String resourceName(String name) {
    if (name != null) {
      String resourceName = name.replace(".", "/");
      resourceName = resourceName.replace("\\", "/");
      if (resourceName.startsWith("/")) {
        resourceName = resourceName.substring(1);
      }
      return resourceName;
    }
    return null;
  }

  private static Collection<URL> distinctUrls(Collection<URL> urls) {
    final Map<String, URL> distinct = new HashMap<>(urls.size());
    for (URL url : urls) {
      distinct.put(url.toExternalForm(), url);
    }
    return distinct.values();
  }
}

