package net.mmm.mc.template.util.reflections.util;

import java.util.regex.Pattern;

import com.google.common.base.Predicate;

/**
 * Created by Lara on 30.07.2019 for template
 */
public abstract class Matcher implements Predicate<String> {
  private final Pattern pattern;

  Matcher(final String regex) {
    pattern = Pattern.compile(regex);
  }

  public abstract boolean apply(String regex);

  @Override
  public String toString() {
    return getPattern().pattern();
  }

  Pattern getPattern() {
    return pattern;
  }
}
