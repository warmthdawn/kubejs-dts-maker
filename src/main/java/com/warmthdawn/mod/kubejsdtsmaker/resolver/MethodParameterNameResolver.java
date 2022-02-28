package com.warmthdawn.mod.kubejsdtsmaker.resolver;

import com.warmthdawn.mod.kubejsdtsmaker.bytecode.MethodMeta;
import com.warmthdawn.mod.kubejsdtsmaker.bytecode.ScanResult;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodParameterNameResolver {

    private Map<MethodMeta, List<String>> parameterMap = new HashMap<>();

    public void acceptScanData(ScanResult result) {
        for (ScanResult.MethodParametersInfo info : result.getMethodParametersInfos()) {
            parameterMap.put(info.getMethodMeta(), info.getParametersName());
        }
    }

    public List<String> find(Method method) {
        return parameterMap.get(MethodMeta.getMeta(method));
    }

    public List<String> find(Constructor<?> constructor) {
        return parameterMap.get(MethodMeta.getMeta(constructor));
    }

}
