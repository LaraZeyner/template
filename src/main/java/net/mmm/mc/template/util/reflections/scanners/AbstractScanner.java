package net.mmm.mc.template.util.reflections.scanners;

import java.util.function.Predicate;

import com.google.common.collect.Multimap;
import net.mmm.mc.template.util.reflections.Configuration;
import net.mmm.mc.template.util.reflections.ReflectionsException;
import net.mmm.mc.template.util.reflections.adapters.MetadataAdapter;
import net.mmm.mc.template.util.reflections.vfs.File;

/**
 *
 */
@SuppressWarnings("RawUseOfParameterizedType")
public abstract class AbstractScanner implements Scanner {

  private Configuration configuration;
  private Multimap<String, String> store;
  private Predicate<String> resultFilter = s -> true; //accept all by default

  public boolean doesAcceptInput(String file) {
    return getMetadataAdapter().doesAcceptsInput(file);
  }

  public Object scan(File file, Object classObject) {
    if (classObject == null) {
      try {
        classObject = configuration.getMetadataAdapter().getOfCreateClassObject(file);
      } catch (Exception e) {
        throw new ReflectionsException("could not create class object from file " + file.getRelativePath());
      }
    }
    scan(classObject);
    return classObject;
  }

  public abstract void scan(Object cls);

  public void setConfiguration(final Configuration configuration) {
    this.configuration = configuration;
  }

  public Multimap<String, String> getStore() {
    return store;
  }

  public void setStore(final Multimap<String, String> store) {
    this.store = store;
  }

  private void setResultFilter(Predicate<String> resultFilter) {
    this.resultFilter = resultFilter;
  }

  public void filterResultsBy(Predicate<String> filter) {
    this.setResultFilter(filter);
  }

  public boolean doesAcceptResult(final String fqn) {
    return fqn != null && resultFilter.test(fqn);
  }

  MetadataAdapter getMetadataAdapter() {
    return configuration.getMetadataAdapter();
  }

  @Override
  public boolean equals(Object o) {
    return this == o || o != null && getClass() == o.getClass();
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
