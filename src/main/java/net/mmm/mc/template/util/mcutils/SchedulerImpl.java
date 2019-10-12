package net.mmm.mc.template.util.mcutils;

import net.mmm.mc.template.Template;
import org.bukkit.Bukkit;

/**
 * Created by Lara on 30.07.2019 for template
 */
abstract class SchedulerImpl {
  private final Object reference;
  private int counter, id;

  SchedulerImpl(Object reference) {
    this.counter = 0;
    this.id = 0;
    this.reference = reference;
  }

  public abstract void invoke();

  void increment(int amount) {
    counter += amount;
  }

  void increment() {
    increment(1);
  }

  void decrement(int amount) {
    increment(-amount);
  }

  void decrement() {
    increment(-1);
  }

  int getCounter() {
    return counter;
  }

  void setCounter(int counter) {
    this.counter = counter;
  }

  Object getReference() {
    return reference;
  }

  void schedule(int startCounter, Runnable runnable) {
    this.counter = startCounter;
    this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(Template.getInstance(), runnable, 20L, 20L);
  }

  void cancel() {
    Bukkit.getScheduler().cancelTask(id);
  }
}
