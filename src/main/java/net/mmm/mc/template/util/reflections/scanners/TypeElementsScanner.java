package net.mmm.mc.template.util.reflections.scanners;

import com.google.common.base.Joiner;

/** scans fields and methods and stores fqn as key and elements as values */
@SuppressWarnings("unchecked")
public class TypeElementsScanner extends AbstractScanner {

  public void scan(Object cls) {
    final String className = getMetadataAdapter().getClassName(cls);
    if (!doesAcceptResult(className)) return;

    getStore().put(className, "");

    for (Object field : getMetadataAdapter().getFields(cls)) {
      final String fieldName = getMetadataAdapter().getFieldName(field);
      getStore().put(className, fieldName);
    }

    for (Object method : getMetadataAdapter().getMethods(cls)) {
      if (getMetadataAdapter().isPublic(method)) {
        final String methodKey = getMetadataAdapter().getMethodName(method) + "(" +
            Joiner.on(", ").join(getMetadataAdapter().getParameterNames(method)) + ")";
        getStore().put(className, methodKey);
      }
    }

    for (Object annotation : getMetadataAdapter().getClassAnnotationNames(cls)) {
      getStore().put(className, "@" + annotation);
    }
  }

}
