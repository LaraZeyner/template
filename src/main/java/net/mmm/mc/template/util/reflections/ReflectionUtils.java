package net.mmm.mc.template.util.reflections;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import net.mmm.mc.template.util.Messenger;
import net.mmm.mc.template.util.reflections.util.ClasspathHelper;

@SuppressWarnings("unchecked")
public abstract class ReflectionUtils {

  public static Class<?> forName(String typeName, ClassLoader... classLoaders) {
    if (getPrimitiveNames().contains(typeName)) {
      return getPrimitiveTypes().get(getPrimitiveNames().indexOf(typeName));
    } else {
      String type;
      if (typeName.contains("[")) {
        final int i = typeName.indexOf('[');
        type = typeName.substring(0, i);
        final String array = typeName.substring(i).replace("]", "");

        type = getPrimitiveNames().contains(type) ?
            getPrimitiveDescriptors().get(getPrimitiveNames().indexOf(type)) : "L" + type + ";";

        type = array + type;
      } else {
        type = typeName;
      }

      final List<ReflectionsException> reflectionsExceptions = Lists.newArrayList();
      for (ClassLoader classLoader : ClasspathHelper.classLoaders(classLoaders)) {
        if (type.contains("[")) {
          try {
            return Class.forName(type, false, classLoader);
          } catch (Throwable e) {
            reflectionsExceptions.add(new ReflectionsException("could not get type for name " + typeName, e));
          }
        }
        try {
          return classLoader.loadClass(type);
        } catch (Throwable e) {
          reflectionsExceptions.add(new ReflectionsException("could not get type for name " + typeName, e));
        }
      }

      for (ReflectionsException reflectionsException : reflectionsExceptions) {
        Messenger.administratorMessage("could not get type for name " + typeName + " from any class loader\n" + reflectionsException.getMessage());
      }

      return null;
    }
  }

  /** try to resolve all given string representation of types to a list of java types */
  static <T> List<Class<? extends T>> forNames(final Iterable<String> classes, ClassLoader... classLoaders) {
    final List<Class<? extends T>> result = new ArrayList<>();
    for (String className : classes) {
      final Class<?> type = forName(className, classLoaders);
      if (type != null) {
        result.add((Class<? extends T>) type);
      }
    }
    return result;
  }

  private static List<String> primitiveNames;
  private static List<Class> primitiveTypes;
  private static List<String> primitiveDescriptors;

  private static void initPrimitives() {
    if (primitiveNames == null) {
      primitiveNames = Lists.newArrayList("boolean", "char", "byte", "short", "int", "long", "float", "double", "void");
      primitiveTypes = Lists.newArrayList(boolean.class, char.class, byte.class, short.class, int.class, long.class, float.class, double.class, void.class);
      primitiveDescriptors = Lists.newArrayList("Z", "C", "B", "S", "I", "J", "F", "D", "V");
    }
  }

  private static List<String> getPrimitiveNames() {
    initPrimitives();
    return primitiveNames;
  }

  private static List<Class> getPrimitiveTypes() {
    initPrimitives();
    return primitiveTypes;
  }

  private static List<String> getPrimitiveDescriptors() {
    initPrimitives();
    return primitiveDescriptors;
  }

}
