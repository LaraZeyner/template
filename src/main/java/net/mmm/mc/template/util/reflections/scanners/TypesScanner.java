package net.mmm.mc.template.util.reflections.scanners;

import net.mmm.mc.template.util.reflections.vfs.File;

/**
 * scans classes and stores fqn as key and full path as value.
 * <p>Deprecated. use {@link TypeElementsScanner}
 */
@Deprecated
public class TypesScanner extends AbstractScanner {
  @Override
  public Object scan(File file, Object classObject) {
    classObject = super.scan(file, classObject);
    final String className = getMetadataAdapter().getClassName(classObject);
    getStore().put(className, className);
    return classObject;
  }

  @Override
  public void scan(Object cls) {
    throw new UnsupportedOperationException("should not get here");
  }
}