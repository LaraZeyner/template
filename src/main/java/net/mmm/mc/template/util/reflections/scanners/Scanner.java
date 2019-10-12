package net.mmm.mc.template.util.reflections.scanners;

import java.util.function.Predicate;

import com.google.common.collect.Multimap;
import net.mmm.mc.template.util.reflections.vfs.File;
import net.mmm.mc.template.util.reflections.Configuration;

public interface Scanner {
  void setConfiguration(Configuration configuration);

  Multimap<String, String> getStore();

  void setStore(Multimap<String, String> store);

  void filterResultsBy(Predicate<String> filter);

  boolean doesAcceptInput(String file);

  Object scan(File file, Object classObject);

  boolean doesAcceptResult(String fqn);
}
