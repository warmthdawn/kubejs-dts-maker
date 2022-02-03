package com.warmthdawn.mod.kubejsgrammardump.utils;

import com.google.common.collect.ImmutableSet;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class JSKeywords {
    public static boolean isKeyword(String id) {
        return keywords.contains(id);
    }

    public static String convertPackageName(String packageName) {
        return Arrays.stream(packageName.split("\\.")).map(JSKeywords::convert).collect(Collectors.joining("."));
    }
    public static String convert(String id) {
        if (isKeyword(id)) {
            return "$" + id;
        }
        return id;
    }

    public static final Set<String> keywords = ImmutableSet.of("break ", "case", "catch", "continue ", "do", "debugger", "default", "delete", "else", "for", "finally", "function", "if", "in", "instanceof", "new", "return", "switch", "this", "throw", "try", "typeof", "var ", "void", "while ", "with");
}
