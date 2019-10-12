package net.mmm.mc.template.util.reflections.util;

import java.util.List;
import java.util.function.Predicate;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class FilterBuilder implements Predicate<String> {
  private final List<java.util.function.Predicate<String>> chain;

  public FilterBuilder() {
    chain = Lists.newArrayList();
  }

  FilterBuilder include(final String regex) {
    return add((Predicate<String>) new Include(regex));
  }

  public Predicate<String> exclude(final String regex) {
    add((Predicate<String>) new FilterBuilder.Exclude(regex));
    return this;
  }

  public FilterBuilder add(java.util.function.Predicate<String> filter) {
    chain.add(filter);
    return this;
  }

  @Override
  public String toString() {
    return Joiner.on(", ").join(chain);
  }

  @Override
  public boolean test(String s) {
    return false;
  }

  private static class Include extends Matcher {
    Include(final String patternString) {
      super(patternString);
    }

    @Override
    public boolean apply(final String regex) {
      return getPattern().matcher(regex).matches();
    }

    @Override
    public String toString() {
      return "+" + super.toString();
    }
  }

  private static class Exclude extends Matcher {
    Exclude(final String patternString) {
      super(patternString);
    }

    @Override
    public boolean apply(final String regex) {
      return !getPattern().matcher(regex).matches();
    }

    @Override
    public String toString() {
      return "-" + super.toString();
    }
  }

}
