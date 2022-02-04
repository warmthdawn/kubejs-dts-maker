package com.warmthdawn.mod.kubejsgrammardump.typescript.type;

import com.warmthdawn.mod.kubejsgrammardump.typescript.ILineBuilder;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.GenericVariable;
import com.warmthdawn.mod.kubejsgrammardump.typescript.generic.GenericVariableProvider;
import com.warmthdawn.mod.kubejsgrammardump.typescript.namespace.Namespace;

import javax.annotation.Nullable;
import java.util.List;

public interface IDeclaredType extends IType, ILineBuilder, GenericVariableProvider {
    Namespace getNamespace();

    default void generateGeneric(StringBuilder builder) {
        List<GenericVariable> genericVariables = this.getVariables();
        if (genericVariables == null) {
            return;
        }
        int lastIndex = genericVariables.size() - 1;
        for (int i = 0; i < genericVariables.size(); i++) {
            if (i == 0) {
                builder.append("<");
            }
            GenericVariable variable = genericVariables.get(i);
            variable.appendTo(builder, this);
            if (i == lastIndex) {
                builder.append(">");
            } else {
                builder.append(", ");
            }
        }
    }

    String getName();

    boolean isGenericClass();
}
