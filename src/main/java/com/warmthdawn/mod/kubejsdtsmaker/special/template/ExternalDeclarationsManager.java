package com.warmthdawn.mod.kubejsdtsmaker.special.template;

import com.warmthdawn.mod.kubejsdtsmaker.KubeJSDtsMaker;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ExternalDeclarationsManager {
    private static ExternalDeclarationsManager instance = new ExternalDeclarationsManager();

    public static ExternalDeclarationsManager getInstance() {
        return instance;
    }

    private Map<String, Map<String, List<String>>> rawDeclarations;

    private Set<String> resourcesToLoad = new HashSet<>();

    public void load() {
        rawDeclarations = new HashMap<>();
        for (String path : resourcesToLoad) {
            try (InputStream in = KubeJSDtsMaker.class.getResourceAsStream(path)) {
                if (in != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                    Map<String, List<String>> result = new HashMap<>();
                    String typeName = null;
                    List<String> lines = null;
                    for (; ; ) {
                        String line = reader.readLine();
                        if (line == null)
                            break;
                        if (line.startsWith("//")) {
                            continue;
                        }

                        if (line.startsWith("type")) {
                            if (lines != null) {
                                result.put(typeName, lines);
                            }
                            typeName = line.substring("type ".length(), line.indexOf("=")).trim();
                            lines = new ArrayList<>();
                        }
                        if (line.startsWith("interface")) {
                            if (lines != null) {
                                result.put(typeName, lines);
                            }
                            typeName = line.substring("interface ".length(), line.indexOf("=")).trim();
                            lines = new ArrayList<>();
                        }
                        if (lines != null && StringUtils.isNotBlank(line)) {
                            lines.add(line);
                        }
                    }
                    rawDeclarations.put(path, result);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

    }


    public ExtraDeclaration register(String resource, String typeName) {
        resourcesToLoad.add(resource);
        return new ExtraDeclaration(typeName, () ->
            Optional.of(rawDeclarations)
                .map(it -> it.get(resource))
                .map(it -> it.get(typeName))
                .orElse(null)
        );
    }


}
