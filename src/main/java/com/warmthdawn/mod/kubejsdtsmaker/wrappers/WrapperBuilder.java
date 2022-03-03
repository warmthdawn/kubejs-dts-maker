package com.warmthdawn.mod.kubejsdtsmaker.wrappers;

import com.warmthdawn.mod.kubejsdtsmaker.KubeJSDtsMaker;
import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.plugins.WrappersPlugin;
import com.warmthdawn.mod.kubejsdtsmaker.special.ISpecialDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.ExtrasDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.TypeAliasDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.*;
import com.warmthdawn.mod.kubejsdtsmaker.util.BuilderUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class WrapperBuilder {
    public Class<?> getTargetClass() {
        return targetClass;
    }

    public static final WrapperBuilder EMPTY = new WrapperBuilder(null, null) {
        @Override
        public Class<?> getTargetClass() {
            return null;
        }

        @Override
        public WrapperBuilder add(TsType type) {
            return this;
        }

        @Override
        public WrapperBuilder add(Class<?> type) {
            return this;
        }

        @Override
        public WrapperBuilder withTemplate(String template) {
            return this;
        }

        @Override
        public WrapperBuilder withTemplateFile(String path) {
            return this;
        }

        @Override
        public WrapperBuilder templateParams(String name, Class<?> clazz) {
            return this;
        }

        @Override
        public void buildAndAdd(BuildContext context, WrapperContext wrapperContext) {

        }
    };

    //WrappingClass
    private Class<?> targetClass;

    private boolean containsSelf;

    private List<TsType> alternativeTypes;
    private List<Class<?>> alternativeClasses;
    private List<Class<?>> referencedTypes;

    private String templateStr;
    private String templateFile;

    private Map<String, Class<?>> templateParameters;
    private String identity;

    private List<ISpecialDeclaration> specialDeclarations;

    public WrapperBuilder(Class<?> targetClass, String identity) {
        this.identity = identity;
        this.targetClass = targetClass;
        this.containsSelf = false;
        this.alternativeTypes = new ArrayList<>();
        this.alternativeClasses = new ArrayList<>();
        this.templateParameters = new HashMap<>();
        this.specialDeclarations = new ArrayList<>();
    }

    public WrapperBuilder ignoreSelf() {
        this.containsSelf = false;
        return this;
    }

    public WrapperBuilder addTuple(TsType... types) {
        this.alternativeTypes.add(new TupleType(Arrays.asList(types)));
        return this;
    }

    public WrapperBuilder addRef(Class<?> clazz) {
        referencedTypes.add(clazz);
        return this;
    }

    public WrapperBuilder addRef() {
        return this;
    }

    public WrapperBuilder addTuple(TsType type, int size) {
        ArrayList<TsType> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add(type);
        }
        this.alternativeTypes.add(new TupleType(list));
        return this;
    }

    public WrapperBuilder addArray(TsType member) {
        this.alternativeTypes.add(new ArrayType(member));
        return this;
    }

    public WrapperBuilder addArray(TsType member, int dimension) {
        this.alternativeTypes.add(new ArrayType(member, dimension));
        return this;
    }

    public WrapperBuilder add(TsType type) {
        this.alternativeTypes.add(type);
        return this;
    }

    public WrapperBuilder addAsEnum() {
        addEnum(targetClass);
        return this;
    }

    public WrapperBuilder addEnum(Class<?> clazz) {
        add(PredefinedType.STRING);
        return this;
    }

    public WrapperBuilder add(String type) {
        this.alternativeTypes.add(new EmbeddedType(type));
        return this;
    }

    public WrapperBuilder add(Class<?> type) {
        this.alternativeClasses.add(type);
        return this;
    }

    public WrapperBuilder nullable() {
        return this;
    }

    public WrapperBuilder acceptAny() {
        this.ignoreSelf();
        this.add(PredefinedType.ANY);
        return this;
    }

    public WrapperBuilder withTemplate(String template) {
        this.templateStr = template;
        return this;
    }

    public WrapperBuilder withTemplateFile(String path) {
        this.templateFile = path;
        return this;
    }

    public WrapperBuilder templateParams(String name, Class<?> clazz) {
        this.templateParameters.put(name, clazz);
        return this;
    }


    public void buildAndAdd(BuildContext context, WrapperContext wrapperContext) {
        List<TsType> types = new ArrayList<>();
        if (containsSelf) {
            TypeReference self = BuilderUtils.createTypeReference(context, targetClass);
            types.add(self);
        }

        types.addAll(alternativeTypes);
        for (Class<?> alternativeClass : alternativeClasses) {
            TypeReference self = BuilderUtils.createTypeReference(context, alternativeClass);
            types.add(self);
        }

        TsType resultType;
        TypeAliasDeclaration wrapperDeclaration = null;
        if (types.size() == 1) {
            resultType = types.get(0);
        } else {
            wrapperDeclaration = new TypeAliasDeclaration(identity, new UnionType(types), null);
            resultType = new TypeReference(null, WrappersPlugin.WRAPPER_NAMESPACE, identity);
        }
        wrapperContext.addWrapperType(targetClass, resultType);

        List<IDeclaration> extraDec = new ArrayList<>();
        if (templateStr == null) {
            if (templateFile != null) {
                try (InputStream in = KubeJSDtsMaker.class.getResourceAsStream(templateFile)) {
                    if (in != null) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                        List<String> lines;
                        if (!templateParameters.isEmpty()) {
                            final String[] searchList = new String[templateParameters.size()];
                            final String[] replacementList = new String[templateParameters.size()];
                            lines = reader.lines().map(
                                it -> StringUtils.replaceEach(it, searchList, replacementList)
                            ).collect(Collectors.toList());
                        } else {
                            lines = reader.lines().collect(Collectors.toList());
                        }
                        extraDec.add(new ExtrasDeclaration(lines));
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        for (ISpecialDeclaration specialDeclaration : specialDeclarations) {
            extraDec.add(specialDeclaration.generate());
        }


        if (wrapperDeclaration != null) {
            wrapperContext.addWrapperDeclaration(wrapperDeclaration);
        }
        wrapperContext.addExtraDeclarations(extraDec);
    }

}
