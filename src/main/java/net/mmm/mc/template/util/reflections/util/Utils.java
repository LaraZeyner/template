package net.mmm.mc.template.util.reflections.util;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;
import net.mmm.mc.template.util.Messenger;

/**
 * a garbage can of convenient methods
 */
public abstract class Utils {

  public static String repeat(String str, int times) {
    final StringBuilder sb = new StringBuilder();

    for (int i = 0; i < times; i++) {
      sb.append(str);
    }

    return sb.toString();
  }

  public static void close(InputStream closeable) {
    try {
      if (closeable != null) closeable.close();
    } catch (IOException ignored) {
        Messenger.administratorMessage("Could not close InputStream");
    }
  }


  private static List<String> names(Iterable<Class<?>> types) {
    final List<String> result = new ArrayList<>();
    for (Class<?> type : types) result.add(type.getSimpleName());
    return result;
  }

  private static List<String> names(Class<?>... types) {
    return names(Arrays.asList(types));
  }

  public static String name(Constructor constructor) {
    return constructor.getName() + "." + "<init>" + "(" + Joiner.on(",").join(names(constructor.getParameterTypes())) + ")";
  }

  public static String name(Method method) {
    return method.getDeclaringClass().getName() + "." + method.getName() + "(" + Joiner.on(", ").join(names(method.getParameterTypes())) + ")";
  }

  public static String name(Field field) {
    return field.getDeclaringClass().getName() + "." + field.getName();
  }
}
