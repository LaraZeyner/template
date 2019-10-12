package net.mmm.mc.template.util.reflections;

import java.net.URL;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.function.Predicate;

import net.mmm.mc.template.util.reflections.adapters.MetadataAdapter;
import net.mmm.mc.template.util.reflections.scanners.Scanner;

public interface Configuration {
    Set<Scanner> getScanners();

    Set<URL> getUrls();

    MetadataAdapter getMetadataAdapter();

    Predicate<String> getInputsFilter();

    ExecutorService getExecutorService();

    ClassLoader[] getClassLoaders();
}
