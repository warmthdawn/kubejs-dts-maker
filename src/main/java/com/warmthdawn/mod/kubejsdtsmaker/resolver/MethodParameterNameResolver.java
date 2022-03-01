package com.warmthdawn.mod.kubejsdtsmaker.resolver;

import com.warmthdawn.mod.kubejsdtsmaker.bytecode.MethodMeta;
import com.warmthdawn.mod.kubejsdtsmaker.bytecode.ScanResult;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodParameterNameResolver {

    //    private Map<MethodMeta, List<String>> parameterMap = new HashMap<>();
    private Map<String, Map<String, Map<String, List<String>>>> parameterMap = new HashMap<>();

    public void acceptScanData(ScanResult result) {
        for (ScanResult.MethodParametersInfo info : result.getMethodParametersInfos()) {
//            parameterMap.put(info.getMethodMeta(), info.getParametersName());
            MethodMeta methodMeta = info.getMethodMeta();
            String methodName = methodMeta.getMethodName();
            String methodDeclaringClass = methodMeta.getMethodDeclaringClass();
            String signature = methodMeta.getSignature();
            parameterMap.computeIfAbsent(methodDeclaringClass, n -> new HashMap<>())
                .computeIfAbsent(methodName, n -> new HashMap<>())
                .put(signature, info.getParametersName());
        }
    }

    private List<String> find(MethodMeta method) {

        String methodName = method.getMethodName();
        String methodDeclaringClass = method.getMethodDeclaringClass();
        String signature = method.getSignature();
        Map<String, Map<String, List<String>>> clazzMap = parameterMap.get(methodDeclaringClass);
        if (clazzMap != null) {
            Map<String, List<String>> memberMap = clazzMap.get(methodName);
            if (memberMap != null) {
                return memberMap.get(signature);
            }
        }
        return null;
//        return parameterMap.get(method);
    }

    public List<String> find(Method method) {
        return find(MethodMeta.getMeta(method));
    }

    public List<String> find(Constructor<?> constructor) {
        return find(MethodMeta.getMeta(constructor));
    }

}
