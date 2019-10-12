package net.mmm.mc.template.util.reflections;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.mmm.mc.template.util.Messenger;
import net.mmm.mc.template.util.reflections.scanners.SubTypesScanner;
import net.mmm.mc.template.util.reflections.util.ConfigurationBuilder;
import net.mmm.mc.template.util.reflections.vfs.Dir;
import net.mmm.mc.template.util.reflections.vfs.File;
import net.mmm.mc.template.util.reflections.vfs.Vfs;
import net.mmm.mc.template.util.reflections.scanners.Scanner;
import static java.lang.String.format;

public class Reflections {
  private final transient Configuration configuration;
  private final Store store;

  private Reflections(final Configuration configuration) {
    this.configuration = configuration;
    store = new Store(configuration);

    if (configuration.getScanners() != null && !configuration.getScanners().isEmpty()) {
      //inject to scanners
      for (Scanner scanner : configuration.getScanners()) {
        scanner.setConfiguration(configuration);
        scanner.setStore(store.getOrCreate(scanner.getClass().getSimpleName()));
      }

      scan();
    }
  }

  public Reflections(final String prefix, final Scanner... scanners) {
    this((Object) prefix, scanners);
  }

  private Reflections(final Object... params) {
    this(ConfigurationBuilder.build(params));
  }

  private void scan() {
    if (configuration.getUrls() == null || configuration.getUrls().isEmpty()) {
      Messenger.administratorMessage("given scan urls are empty. set urls in the configuration");
      return;
    }

    Messenger.administratorMessage("going to scan these urls:\n" + Joiner.on("\n").join(configuration.getUrls()));

    long time = System.currentTimeMillis();
    int scannedUrls = 0;
    final ExecutorService executorService = configuration.getExecutorService();
    final List<Future<?>> futures = Lists.newArrayList();

    for (final URL url : configuration.getUrls()) {
      try {
        if (executorService != null) {
          futures.add(executorService.submit(() -> {
            Messenger.administratorMessage("[" + Thread.currentThread() + "] scanning " + url);
            scan(url);
          }));
        } else {
          scan(url);
        }
        scannedUrls++;
      } catch (ReflectionsException ignored) {
        Messenger.administratorMessage("could not create Vfs.Dir from url. ignoring the exception and continuing");
      }
    }

    if (executorService != null) {
      for (Future future : futures) {
        try {
          future.get();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    }

    time = System.currentTimeMillis() - time;

    int keys = 0;
    int values = 0;
    for (String index : store.keySet()) {
      keys += store.get(index).keySet().size();
      values += store.get(index).size();
    }
    Messenger.administratorMessage(format("Reflections took %d ms to scan %d urls, producing %d keys and %d values %s",
        time, scannedUrls, keys, values,
        executorService instanceof ThreadPoolExecutor ?
            format("[using %d cores]", ((ThreadPoolExecutor) executorService).getMaximumPoolSize()) : ""));
  }

  private void scan(URL url) {
    final Dir dir = Vfs.fromURL(url);

    try {
      for (final File file : dir.getFiles()) {
        // scan if inputs filter accepts file relative path or fqn
        final java.util.function.Predicate<String> inputsFilter = configuration.getInputsFilter();
        final String path = file.getRelativePath();
        final String fqn = path.replace('/', '.');
        if (inputsFilter.test(path) || inputsFilter.test(fqn)) {
          Object classObject = null;
          for (Scanner scanner : configuration.getScanners()) {
            try {
              if (scanner.doesAcceptInput(path) || scanner.doesAcceptResult(fqn)) {
                classObject = scanner.scan(file, classObject);
              }
            } catch (Exception e) {
              Messenger.administratorMessage("could not scan file " + file.getRelativePath() + " in url " + url.toExternalForm() + " with scanner " + scanner.getClass().getSimpleName());
            }
          }
        }
      }
    } finally {
      dir.close();
    }
  }

  public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type) {
    return Sets.newHashSet(ReflectionUtils.forNames(
        store.getAll(index(), Collections.singletonList(type.getName())), loaders()));
  }

  private static String index() {
    return SubTypesScanner.class.getSimpleName();
  }

  private ClassLoader[] loaders() {
    return configuration.getClassLoaders();
  }

  Store getStore() {
    return store;
  }
}
