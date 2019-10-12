package net.mmm.mc.template.util.reflections.adapters;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import net.mmm.mc.template.util.reflections.ReflectionUtils;
import net.mmm.mc.template.util.reflections.vfs.File;
import net.mmm.mc.template.util.reflections.util.Utils;

/**
 *
 */
public class JavaReflectionAdapter implements MetadataAdapter<Class, Field, Member> {

  public List<Field> getFields(Class cls) {
    return Lists.newArrayList(cls.getDeclaredFields());
  }

  public List<Member> getMethods(Class cls) {
    final List<Member> methods = Lists.newArrayList();
    methods.addAll(Arrays.asList(cls.getDeclaredMethods()));
    methods.addAll(Arrays.asList(cls.getDeclaredConstructors()));
    return methods;
  }

  public String getMethodName(Member method) {
    return method instanceof Method ? method.getName() :
        method instanceof Constructor ? "<init>" : null;
  }

  public List<String> getParameterNames(final Member member) {
    final List<String> result = Lists.newArrayList();

    final Class<?>[] parameterTypes = member instanceof Method ? ((Method) member).getParameterTypes() :
        member instanceof Constructor ? ((Constructor) member).getParameterTypes() : null;

    if (parameterTypes != null) {
      for (Class<?> paramType : parameterTypes) {
        final String name = getName(paramType);
        result.add(name);
      }
    }

    return result;
  }

  public List<String> getClassAnnotationNames(Class aClass) {
    return getAnnotationNames(aClass.getDeclaredAnnotations());
  }

  public String getFieldName(Field field) {
    return field.getName();
  }

  public Class getOfCreateClassObject(File file) {
    return getOfCreateClassObject(file, (ClassLoader) null);
  }

  private Class getOfCreateClassObject(File file, ClassLoader... loaders) {
    final String name = file.getRelativePath().replace("/", ".").replace(".class", "");
    return ReflectionUtils.forName(name, loaders);
  }

  public boolean isPublic(Object o) {
    final int mod = o instanceof Class ? ((Class) o).getModifiers() :
        o instanceof Member ? ((Member) o).getModifiers() : null;

    return Modifier.isPublic(mod);
  }

  public String getClassName(Class cls) {
    return cls.getName();
  }

  public String getSuperclassName(Class cls) {
    final Class superclass = cls.getSuperclass();
    return superclass != null ? superclass.getName() : "";
  }

  public List<String> getInterfacesNames(Class cls) {
    final Class[] classes = cls.getInterfaces();
    final List<String> names = new ArrayList<>(classes != null ? classes.length : 0);
    if (classes != null) for (Class cls1 : classes) names.add(cls1.getName());
    return names;
  }

  public boolean doesAcceptsInput(String file) {
    return file.endsWith(".class");
  }

  private List<String> getAnnotationNames(Annotation[] annotations) {
    final List<String> names = new ArrayList<>(annotations.length);
    for (Annotation annotation : annotations) {
      names.add(annotation.annotationType().getName());
    }
    return names;
  }

  public static String getName(Class type) {
    if (type.isArray()) {
      try {
        Class cl = type;
        int dim = 0;
        while (cl.isArray()) {
          dim++;
          cl = cl.getComponentType();
        }
        return cl.getName() + Utils.repeat("[]", dim);
      } catch (Throwable e) {
        //
      }
    }
    return type.getName();
  }
}
