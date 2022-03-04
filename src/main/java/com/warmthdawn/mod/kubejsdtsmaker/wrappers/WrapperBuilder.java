package com.warmthdawn.mod.kubejsdtsmaker.wrappers;

import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.plugins.WrappersPlugin;
import com.warmthdawn.mod.kubejsdtsmaker.special.ISpecialDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.TypeAliasDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeArguments;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.*;
import com.warmthdawn.mod.kubejsdtsmaker.util.BuilderUtils;
import net.minecraft.util.Tuple;

import java.util.*;
import java.util.function.Function;

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
        public void buildAndAdd(BuildContext context, WrapperContext wrapperContext) {

        }
    };

    //WrappingClass
    private Class<?> targetClass;

    private boolean containsSelf;
    private boolean isNullable = false;

    private List<TsType> alternativeTypes;
    private List<Class<?>> alternativeClasses;
    private List<Class<?>> referencedTypes;
    private Tuple<String, String> wrap;


    private String identity;

    private List<ISpecialDeclaration> specialDeclarations;

    public WrapperBuilder(Class<?> targetClass, String identity) {
        this.identity = identity;
        this.targetClass = targetClass;
        this.containsSelf = true;
        this.alternativeTypes = new ArrayList<>();
        this.alternativeClasses = new ArrayList<>();
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


    public WrapperBuilder addRef(WrapperBuilder regExp) {
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

    public WrapperBuilder addAsEnum(Function<Object, String> literalFunction) {
        addEnum(targetClass, literalFunction);
        return this;
    }
    public WrapperBuilder addLiterals(String ...literals) {
        addLiterals(Arrays.asList(literals));
        return this;
    }
    public WrapperBuilder addLiterals(Collection<String> literals) {
        add(BuilderUtils.createStringLiterals(new ArrayList<>(literals)));
        return this;
    }

    public WrapperBuilder addEnum(Class<?> clazz) {
        add(BuilderUtils.createStringLiterals(clazz));
        return this;
    }

    public WrapperBuilder addEnum(Class<?> clazz, Function<Object, String> literalFunction) {
        add(BuilderUtils.createStringLiterals(clazz, literalFunction));
        return this;
    }

    public WrapperBuilder withWrap(String wrapName, String wrapNamespace) {
        wrap = new Tuple<>(wrapName, wrapNamespace);
        return this;
    }

    public WrapperBuilder withWrap(ISpecialDeclaration specialDeclaration) {
        specialDeclarations.add(specialDeclaration);
        withWrap(specialDeclaration.getIdentity(), WrappersPlugin.EXTRA_NAMESPACE);
        return this;
    }

    public WrapperBuilder addExtra(ISpecialDeclaration specialDeclaration) {
        this.alternativeTypes.add(new TypeReference(null, WrappersPlugin.EXTRA_NAMESPACE, specialDeclaration.getIdentity()));
        specialDeclarations.add(specialDeclaration);
        return this;
    }

    public WrapperBuilder addSpecialExtra(ISpecialDeclaration specialDeclaration) {
        specialDeclarations.add(specialDeclaration);
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
        this.isNullable = true;
        return this;
    }

    public WrapperBuilder acceptAny() {
        this.ignoreSelf();
        this.add(PredefinedType.ANY);
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
            TsType decType = new UnionType(types);
            if (wrap != null) {
                decType = new TypeReference(new TypeArguments(decType), wrap.getB(), wrap.getA());
            }
            wrapperDeclaration = new TypeAliasDeclaration(identity, decType, null);
            resultType = new TypeReference(null, WrappersPlugin.WRAPPER_NAMESPACE, identity);
        }
        wrapperContext.addWrapperType(targetClass, resultType);


        if (wrapperDeclaration != null) {
            wrapperContext.addWrapperDeclaration(wrapperDeclaration);
        }
    }

}
