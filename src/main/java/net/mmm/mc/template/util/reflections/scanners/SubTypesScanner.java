package net.mmm.mc.template.util.reflections.scanners;

import java.util.List;

import net.mmm.mc.template.util.reflections.util.FilterBuilder;

public class SubTypesScanner extends AbstractScanner {
  public SubTypesScanner() {
    this(true); //exclude direct Object subtypes by default
  }

  private SubTypesScanner(boolean excludeObjectClass) {
    if (excludeObjectClass) {
      filterResultsBy(new FilterBuilder().exclude(Object.class.getName())); //exclude direct Object subtypes
    }
  }

  @SuppressWarnings("unchecked")
  public void scan(final Object cls) {
    final String className = getMetadataAdapter().getClassName(cls);
    final String superclass = getMetadataAdapter().getSuperclassName(cls);

    if (doesAcceptResult(superclass)) {
      getStore().put(superclass, className);
    }

    for (String anInterface : (List<String>) getMetadataAdapter().getInterfacesNames(cls)) {
      if (doesAcceptResult(anInterface)) {
        getStore().put(anInterface, className);
      }
    }
  }
}
