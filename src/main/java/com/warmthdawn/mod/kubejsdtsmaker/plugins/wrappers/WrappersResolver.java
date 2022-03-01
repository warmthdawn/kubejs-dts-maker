package com.warmthdawn.mod.kubejsdtsmaker.plugins.wrappers;

import java.util.Map;

public class WrappersResolver {


    public void resolveWrappers(Map<Class<?>, Object[]> wrappersCfg) {

    }

    public void resolveWrapper(Class<?> clazz, Object[] config) {

        for (Object o : config) {

            if (o instanceof Class<?>) {

            } else if (o instanceof String) {

            }


        }


    }

}
