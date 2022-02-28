package com.warmthdawn.mod.kubejsdtsmaker.plugins;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.warmthdawn.mod.kubejsdtsmaker.BuilderManager;
import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.builder.TypescriptFactory;
import com.warmthdawn.mod.kubejsdtsmaker.bytecode.ScanResult;
import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.global.IGlobalDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TypeReference;
import dev.latvian.kubejs.event.EventJS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;

import java.util.*;

public class RecipeEventPlugin implements IBuilderPlugin {
    private static final Logger logger = LogManager.getLogger();
    private final Map<String, Class<?>> eventClasses = new HashMap<>();
    private final Map<String, String> eventNameMap = new HashMap<>();
    private TypescriptFactory typescriptFactory;

    @Override
    public void init(BuilderManager manager) {
        this.typescriptFactory = manager.getTypescriptFactory();
    }

    @Override
    public void acceptScanData(ScanResult result) {

        List<ScanResult.ClassInfo> classInfos = result.getClassInfos();
        resolveEventClasses(classInfos);
        List<ScanResult.EventPostInfo> eventPostInfos = result.getEventPostInfos();
        resolveEventNameMapping(eventPostInfos);

    }

    private void resolveEventClasses(List<ScanResult.ClassInfo> classInfos) {
        Multimap<String, String> clazzMaps = HashMultimap.create();

        for (ScanResult.ClassInfo classInfo : classInfos) {
            if (classInfo.getSuperClassName() != null) {
                clazzMaps.put(classInfo.getSuperClassName(), classInfo.getClassName());
            }
        }
        String baseName = Type.getType(EventJS.class).getInternalName();

        findAllIterable(clazzMaps, baseName);

    }

    private void findAllIterable(Multimap<String, String> clazzMaps, String name) {
        try {
            String className = Type.getObjectType(name).getClassName();
            Class<?> clazz = Class.forName(className);
            eventClasses.put(name, clazz);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            logger.error("Could not find event class {} when resolving", name, e);
        }
        Collection<String> collection = clazzMaps.get(name);
        for (String s : collection) {
            findAllIterable(clazzMaps, s);
        }
    }


    private void resolveEventNameMapping(List<ScanResult.EventPostInfo> eventPostInfos) {
        for (ScanResult.EventPostInfo eventPostInfo : eventPostInfos) {
            String clazzName = eventPostInfo.getEventClass();
            String eventName = eventPostInfo.getEventName();
            if (!eventClasses.containsKey(clazzName)) {
                logger.error("Event {} post a class {} didn't seems to be a subclass of EventJS", eventName, clazzName);
                continue;
            }
            eventNameMap.put(eventName, clazzName);
        }
    }

    @Override
    public void addResolveClass(Set<Class<?>> resolveClass) {
        resolveClass.addAll(eventClasses.values());
    }

    @Override
    public void addExtraGlobals(List<IGlobalDeclaration> output) {
        Map<String, TsType> eventMaps = new HashMap<>();
        for (Map.Entry<String, String> entry : eventNameMap.entrySet()) {
            Class<?> clazz = eventClasses.get(entry.getValue());
            TsType reference = typescriptFactory.createReferenceNonnull(clazz);
            eventMaps.put(entry.getKey(), reference);
        }
        TsType baseEventType = typescriptFactory.createReferenceNonnull(EventJS.class);
        output.add(new GlobalOnEventMethodDeclaration("onEvent", "KubeJSEventMappings", eventMaps, baseEventType));
    }

    public static class GlobalOnEventMethodDeclaration implements IGlobalDeclaration {
        private String methodName;
        private String typeMapName;
        private Map<String, TsType> eventMaps;
        private TsType baseEventType;

        public GlobalOnEventMethodDeclaration(String methodName, String typeMapName, Map<String, TsType> eventMaps, TsType baseEventType) {
            this.methodName = methodName;
            this.typeMapName = typeMapName;
            this.eventMaps = eventMaps;
            this.baseEventType = baseEventType;
        }

        @Override
        public void build(DeclarationBuilder builder) {
            builder
                .append("declare function ")
                .append(methodName)
                .append("<K extends keyof ")
                .append(typeMapName)
                .append(">(event: K | K[], handler: (event: ")
                .append(typeMapName)
                .append("[K]) => void): void")
                .newLine()
                .append("declare function ")
                .append(methodName)
                .append("(event: string | string[], handler: (event: ")
                .append(baseEventType)
                .append(") => void): void")
                .newLine()
                .append("type ").append(typeMapName).append(" = {")
                .increaseIndent();
            eventMaps.forEach((k, v) -> {
                builder.newLine()
                    .append("'").append(k).append("': ")
                    .append(v)
                    .append(",");
            });
            builder.decreaseIndent().newLine().append("}").newLine();
        }
    }
}
