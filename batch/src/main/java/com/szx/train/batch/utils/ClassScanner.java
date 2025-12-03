package com.szx.train.batch.utils;

import org.quartz.Job;
import org.reflections.Reflections;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author ferry
 * @date 2025/12/1
 * @project train
 * @description
 */
@Component
public class ClassScanner {
    public static Set<Class<? extends Job>> getClassesInPackage(String packageName) {
        Reflections reflections = new Reflections(packageName);
        return reflections.getSubTypesOf(org.quartz.Job.class);
    }

    public static List<String> ScanPackage(String packageName) {
        Set<Class<? extends Job>> classes = getClassesInPackage(packageName);
        ArrayList<String> strings = new ArrayList<>();
        for (Class<?> clazz : classes) {
            strings.add(clazz.getName());
        }
        return strings;
    }
}