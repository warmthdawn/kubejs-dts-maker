package com.warmthdawn.mod.kubejsdtsmaker.special.template;

import com.warmthdawn.mod.kubejsdtsmaker.special.ExtraDeclarations;
import com.warmthdawn.mod.kubejsdtsmaker.special.ISpecialDeclaration;

import java.util.Arrays;

public class ExtraDeclarationsUtils {

    private static String buildString(String... strs) {
        StringBuilder builder = new StringBuilder();
        for (String str : strs) {
            builder.append(str);
        }
        return builder.toString();
    }

    private static String buildTypeString(String identity, String... strs) {
        StringBuilder builder = new StringBuilder();
        builder.append("type ").append(identity).append(" = ");
        for (String str : strs) {
            builder.append(str);
        }
        builder.append(";");
        return builder.toString();
    }

    public static ExtraDeclaration external(String file, String identity) {
        if (!file.contains("/")) {
            file = "data/kubejsdtsmaker/wrappers/" + identity;
        }
        return ExternalDeclarationsManager.getInstance().register(file, identity);
    }

    public static ExtraDeclaration raw(String identity, String... lines) {
        return new ExtraDeclaration(identity, () -> Arrays.asList(lines));
    }

    public static ExtraDeclaration simple(String identity, String declaration) {
        return raw(identity, buildTypeString(identity, declaration));
    }


    public static ExtraDeclaration wrap(String identity, ISpecialDeclaration wrapper, ISpecialDeclaration member) {
        String def = buildTypeString(identity, wrapper.getIdentity(), "<", member.getIdentity(), ">");
        return raw(identity, def)
            .withDependencies(wrapper.getIdentity(), member.getIdentity());
    }


    public static ExtraDeclaration prefix(String identity, ISpecialDeclaration type, String prefix) {
        String def = buildTypeString(identity, "`", prefix, "${", type.getIdentity(), "}`");

        return raw(identity, def)
            .withDependencies(type.getIdentity());
    }
}
