package com.warmthdawn.mod.kubejsdtsmaker.bytecode;

import org.objectweb.asm.Type;

import java.util.ArrayList;
import java.util.List;

public class ScanResult {

    private final List<EventPostInfo> eventPostInfos = new ArrayList<>();
    private final List<ClassInfo> classInfos = new ArrayList<>();
    private final List<MethodParametersInfo> methodParametersInfos = new ArrayList<>();

    public List<EventPostInfo> getEventPostInfos() {
        return eventPostInfos;
    }

    public List<ClassInfo> getClassInfos() {
        return classInfos;
    }

    public List<MethodParametersInfo> getMethodParametersInfos() {
        return methodParametersInfos;
    }

    public void addEventPostInfo(String eventClass, String eventName) {
        eventPostInfos.add(new EventPostInfo(eventClass, eventName));
    }

    public void addClassInfo(String className, String superClassName) {
        classInfos.add(new ClassInfo(className, superClassName));
    }

    public void addMethodParametersInfo(String methodName, String methodDeclaringClass, String signature, List<String> parametersName) {
        methodParametersInfos.add(new MethodParametersInfo(new MethodMeta(methodName, methodDeclaringClass, signature), parametersName));
    }


    public static class ClassInfo {
        private String className;
        private String superClassName;

        public String getClassName() {
            return className;
        }

        public String getSuperClassName() {
            return superClassName;
        }

        public ClassInfo(String className, String superClassName) {
            this.className = className;
            this.superClassName = superClassName;
        }
    }

    public static class MethodParametersInfo {
        private MethodMeta methodMeta;
        private List<String> parametersName;

        public MethodParametersInfo(MethodMeta methodMeta, List<String> parametersName) {
            this.methodMeta = methodMeta;
            this.parametersName = parametersName;
        }

        public MethodMeta getMethodMeta() {
            return methodMeta;
        }

        public List<String> getParametersName() {
            return parametersName;
        }
    }

    public static class EventPostInfo {
        private String eventClass;
        private String eventName;

        public String getEventClass() {
            return eventClass;
        }

        public String getEventName() {
            return eventName;
        }

        public EventPostInfo(String eventClass, String eventName) {
            this.eventClass = eventClass;
            this.eventName = eventName;
        }
    }
}
