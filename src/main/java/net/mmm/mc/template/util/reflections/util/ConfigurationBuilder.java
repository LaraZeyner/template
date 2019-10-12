package net.mmm.mc.template.util.reflections.util;

import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import net.mmm.mc.template.util.Messenger;
import net.mmm.mc.template.util.reflections.adapters.JavassistAdapter;
import net.mmm.mc.template.util.reflections.Configuration;
import net.mmm.mc.template.util.reflections.ReflectionsException;
import net.mmm.mc.template.util.reflections.adapters.JavaReflectionAdapter;
import net.mmm.mc.template.util.reflections.adapters.MetadataAdapter;
import net.mmm.mc.template.util.reflections.scanners.Scanner;
import net.mmm.mc.template.util.reflections.scanners.SubTypesScanner;
import net.mmm.mc.template.util.reflections.scanners.TypeAnnotationsScanner;

public final class ConfigurationBuilder implements Configuration {
  private final Set<Scanner> scanners;
  private final Set<URL> urls;
  private MetadataAdapter metadataAdapter;
  private Predicate<String> inputsFilter;
  private ExecutorService executorService;
  private ClassLoader[] classLoaders;

  private ConfigurationBuilder() {
    scanners = Sets.newHashSet(new TypeAnnotationsScanner(), new SubTypesScanner());
    urls = Sets.newHashSet();
  }

  @SuppressWarnings("unchecked")
  public static ConfigurationBuilder build(final Object... params) {
    final ConfigurationBuilder builder = new ConfigurationBuilder();

    //flatten
    final List<Object> parameters = Lists.newArrayList();
    if (params != null) {
      for (Object param : params) {
        if (param != null) {
          if (param.getClass().isArray()) {
            for (Object p : (Object[]) param) if (p != null) parameters.add(p);
          } else if (param instanceof Iterable) {
            for (Object p : (Iterable) param) if (p != null) parameters.add(p);
          } else parameters.add(param);
        }
      }
    }

    final List<ClassLoader> loaders = Lists.newArrayList();
    for (Object param : parameters) if (param instanceof ClassLoader) loaders.add((ClassLoader) param);

    final ClassLoader[] classLoaders = loaders.isEmpty() ? null : loaders.toArray(new ClassLoader[0]);
    final FilterBuilder filter = new FilterBuilder();
    final List<Scanner> scanners = Lists.newArrayList();

    for (Object param : parameters) {
      if (param instanceof String) {
        builder.addUrls(ClasspathHelper.forPackage((String) param, classLoaders));
        filter.include((String) param);
      } else if (param instanceof Class) {
        if (Scanner.class.isAssignableFrom((Class) param)) {
          try {
            builder.addScanners(((Scanner) ((Class) param).getConstructor().newInstance()));
          } catch (Exception e) { /*fallback*/ }
        }
        builder.addUrls(ClasspathHelper.forClass((Class) param, classLoaders));
        filter.include(((Class) param).getName());
      } else if (param instanceof Scanner) {
        scanners.add((Scanner) param);
      } else if (param instanceof URL) {
        builder.addUrls((URL) param);
      } else if (param instanceof ClassLoader) { /* already taken care */ } else if (param instanceof Predicate) {
        filter.add((Predicate<String>) param);
      } else if (param instanceof ExecutorService) {
        builder.setExecutorService((ExecutorService) param);
      } else {
        throw new ReflectionsException("could not use param " + param);
      }
    }

    if (builder.getUrls().isEmpty()) {
      if (classLoaders != null) {
        builder.addUrls(ClasspathHelper.forClassLoader(classLoaders)); //default urls getResources("")
      } else {
        builder.addUrls(ClasspathHelper.forClassLoader()); //default urls getResources("")
      }
    }

    builder.filterInputsBy(filter);
    if (!scanners.isEmpty()) {
      builder.setScanners(scanners.toArray(new Scanner[0]));
    }
    if (!loaders.isEmpty()) {
      builder.addClassLoaders(loaders);
    }

    return builder;
  }

  public Set<Scanner> getScanners() {
    return scanners;
  }

  /** set the scanners instances for scanning different metadata */
  private void setScanners(Scanner... scanners) {
    this.scanners.clear();
    addScanners(scanners);
  }

  /** set the scanners instances for scanning different metadata */
  private void addScanners(final Scanner... scanners) {
    this.scanners.addAll(Sets.newHashSet(scanners));
  }

  public Set<URL> getUrls() {
    return urls;
  }

  private void addUrls(final Collection<URL> urls) {
    this.urls.addAll(urls);
  }

  private void addUrls(final URL... urls) {
    this.urls.addAll(Sets.newHashSet(urls));
  }

  public MetadataAdapter getMetadataAdapter() {
    if (metadataAdapter != null) return metadataAdapter;
    else {
      try {
        return (metadataAdapter = new JavassistAdapter());
      } catch (Throwable ignored) {
        Messenger.administratorMessage("could not create JavassistAdapter, using JavaReflectionAdapter");
        return (metadataAdapter = new JavaReflectionAdapter());
      }
    }
  }

  public java.util.function.Predicate<String> getInputsFilter() {
    return inputsFilter;
  }

  /**
   * sets the input filter for all resources to be scanned.
   * <p> supply a {@link Predicate} or use the {@link FilterBuilder}
   */
  private void filterInputsBy(java.util.function.Predicate<String> inputsFilter) {
    this.inputsFilter = inputsFilter;
  }

  public ExecutorService getExecutorService() {
    return executorService;
  }

  /** sets the executor service used for scanning. */
  private void setExecutorService(ExecutorService executorService) {
    this.executorService = executorService;
  }

  /** get class loader, might be used for scanning or resolving methods/fields */
  public ClassLoader[] getClassLoaders() {
    return classLoaders;
  }

  /** add class loader, might be used for resolving methods/fields */
  private void addClassLoaders(ClassLoader... classLoaders) {
    this.classLoaders = this.classLoaders == null ? classLoaders : ObjectArrays.concat(this.classLoaders, classLoaders, ClassLoader.class);
  }

  /** add class loader, might be used for resolving methods/fields */
  private void addClassLoaders(Collection<ClassLoader> classLoaders) {
    addClassLoaders(classLoaders.toArray(new ClassLoader[0]));
  }
}
