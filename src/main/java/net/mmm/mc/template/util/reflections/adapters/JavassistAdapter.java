package net.mmm.mc.template.util.reflections.adapters;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;
import net.mmm.mc.template.util.reflections.ReflectionsException;
import net.mmm.mc.template.util.reflections.util.Utils;
import net.mmm.mc.template.util.reflections.vfs.File;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.Descriptor;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;

/**
 *
 */
public class JavassistAdapter implements MetadataAdapter<ClassFile, FieldInfo, MethodInfo> {

  /** setting this to false will result in returning only visible annotations from the relevant methods here (only {@link java.lang.annotation.RetentionPolicy#RUNTIME}) */
  private static final boolean includeInvisibleTag = true;

  public List<FieldInfo> getFields(final ClassFile cls) {
    return cls.getFields();
  }

  public List<MethodInfo> getMethods(final ClassFile cls) {
    return cls.getMethods();
  }

  public String getMethodName(final MethodInfo method) {
    return method.getName();
  }

  public List<String> getParameterNames(final MethodInfo method) {
    String descriptor = method.getDescriptor();
    descriptor = descriptor.substring(descriptor.indexOf('(') + 1, descriptor.lastIndexOf(')'));
    return splitDescriptorToTypeNames(descriptor);
  }

  public List<String> getClassAnnotationNames(final ClassFile aClass) {
    return getAnnotationNames((AnnotationsAttribute) aClass.getAttribute(AnnotationsAttribute.visibleTag),
        includeInvisibleTag ? (AnnotationsAttribute) aClass.getAttribute(AnnotationsAttribute.invisibleTag) : null);
  }

  public String getFieldName(final FieldInfo field) {
    return field.getName();
  }

  public ClassFile getOfCreateClassObject(final File file) {
    InputStream inputStream = null;
    try {
      inputStream = file.openInputStream();
      final DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream));
      return new ClassFile(dis);
    } catch (IOException e) {
      throw new ReflectionsException("could not create class file from " + file.getName(), e);
    } finally {
      Utils.close(inputStream);
    }
  }

  public boolean isPublic(Object o) {
    final int accessFlags =
        o instanceof ClassFile ? ((ClassFile) o).getAccessFlags() :
            o instanceof FieldInfo ? ((FieldInfo) o).getAccessFlags() :
                o instanceof MethodInfo ? ((MethodInfo) o).getAccessFlags() : null;

    return AccessFlag.isPublic(accessFlags);
  }

  public String getClassName(final ClassFile cls) {
    return cls.getName();
  }

  public String getSuperclassName(final ClassFile cls) {
    return cls.getSuperclass();
  }

  public List<String> getInterfacesNames(final ClassFile cls) {
    return Arrays.asList(cls.getInterfaces());
  }

  public boolean doesAcceptsInput(String file) {
    return file.endsWith(".class");
  }

  //
  private List<String> getAnnotationNames(final AnnotationsAttribute... annotationsAttributes) {
    final List<String> result = Lists.newArrayList();

    if (annotationsAttributes != null) {
      for (AnnotationsAttribute annotationsAttribute : annotationsAttributes) {
        if (annotationsAttribute != null) {
          for (Annotation annotation : annotationsAttribute.getAnnotations()) {
            result.add(annotation.getTypeName());
          }
        }
      }
    }

    return result;
  }

  private List<String> splitDescriptorToTypeNames(final String descriptors) {
    final List<String> result = Lists.newArrayList();

    if (descriptors != null && !descriptors.isEmpty()) {

      final List<Integer> indices = Lists.newArrayList();
      final Descriptor.Iterator iterator = new Descriptor.Iterator(descriptors);
      while (iterator.hasNext()) {
        indices.add(iterator.next());
      }
      indices.add(descriptors.length());

      for (int i = 0; i < indices.size() - 1; i++) {
        final String s1 = Descriptor.toString(descriptors.substring(indices.get(i), indices.get(i + 1)));
        result.add(s1);
      }

    }

    return result;
  }
}
