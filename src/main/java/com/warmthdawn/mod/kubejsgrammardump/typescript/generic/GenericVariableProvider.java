package com.warmthdawn.mod.kubejsgrammardump.typescript.generic;

import java.util.List;

public interface GenericVariableProvider {
    List<GenericVariable> getVariables();
    boolean containsVariable(String name);
}
