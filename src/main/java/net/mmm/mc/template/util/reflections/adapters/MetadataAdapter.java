package net.mmm.mc.template.util.reflections.adapters;

import java.util.List;

import net.mmm.mc.template.util.reflections.vfs.File;

public interface MetadataAdapter<C, F, M> {
  String getClassName(final C cls);

  String getSuperclassName(final C cls);

  List<String> getInterfacesNames(final C cls);

  List<F> getFields(final C cls);

  List<M> getMethods(final C cls);

  String getMethodName(final M method);

  List<String> getParameterNames(final M method);

  List<String> getClassAnnotationNames(final C aClass);

  String getFieldName(final F field);

  C getOfCreateClassObject(File file) throws Exception;

  boolean isPublic(Object o);

  boolean doesAcceptsInput(String file);

}
