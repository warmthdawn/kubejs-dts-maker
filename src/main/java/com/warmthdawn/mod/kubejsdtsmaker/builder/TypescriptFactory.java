package com.warmthdawn.mod.kubejsdtsmaker.builder;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.warmthdawn.mod.kubejsdtsmaker.BuilderManager;
import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.context.GlobalTypeScope;
import com.warmthdawn.mod.kubejsdtsmaker.context.ResolveContext;
import com.warmthdawn.mod.kubejsdtsmaker.java.JavaConstructorMember;
import com.warmthdawn.mod.kubejsdtsmaker.java.JavaInstanceMember;
import com.warmthdawn.mod.kubejsdtsmaker.java.JavaStaticMember;
import com.warmthdawn.mod.kubejsdtsmaker.java.JavaTypeInfo;
import com.warmthdawn.mod.kubejsdtsmaker.resolver.MethodParameterNameResolver;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.DeclarationFile;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.Namespace;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.IDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.InterfaceDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.TypeAliasDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeArguments;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeParameter;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeParameters;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.member.*;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.member.Member;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.misc.CallSignature;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.misc.TsConstructorSignature;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.*;
import com.warmthdawn.mod.kubejsdtsmaker.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

public class TypescriptFactory {
    private final ResolveContext context;
    private final BuildContext buildContext;
    private final MethodParameterNameResolver parameterNameResolver;
    private static final Logger logger = LogManager.getLogger();
    private final BuilderManager manager;

    public TypescriptFactory(BuilderManager manager, ResolveContext context, BuildContext buildContext, MethodParameterNameResolver parameterNameResolver) {
        this.context = context;
        this.buildContext = buildContext;
        this.parameterNameResolver = parameterNameResolver;
        this.manager = manager;
    }

    public BuildContext getBuildContext() {
        return buildContext;
    }

    public DeclarationFile createFile() {
        Map<Class<?>, JavaTypeInfo> allTypes = context.getTypeInfos();
        Multimap<String, Class<?>> groupedTypes = TreeMultimap.create(Comparator.naturalOrder(), Comparator.comparing(Class::getSimpleName));

        //吧所有类按命名空间分组
        for (Class<?> clazz : allTypes.keySet()) {
            String namespace = buildContext.getNamespace(clazz);
            groupedTypes.put(namespace, clazz);
        }
        List<Namespace> namespaces = new ArrayList<>(groupedTypes.keySet().size());
        for (String namespaceName : groupedTypes.keySet()) {
            Namespace namespace = createNamespace(namespaceName, groupedTypes.get(namespaceName));
            namespaces.add(namespace);
        }
        buildContext.setLoaded(true);
        return new DeclarationFile(namespaces);
    }

    public Namespace createNamespace(String name, Collection<Class<?>> classes) {
        List<IDeclaration> children = new ArrayList<>(classes.size());
        for (Class<?> clazz : classes) {
            JavaTypeInfo typeInfo = context.get(clazz);
            if (typeInfo == null) {
                continue;
            }
            IDeclaration declaration = createTypeDeclaration(typeInfo);
            children.add(declaration);

            InterfaceDeclaration staticDeclaration = createStaticTypeDeclaration(name, typeInfo);
            if (staticDeclaration != null) {
                children.add(staticDeclaration);
            }
            buildContext.addType(clazz, declaration, staticDeclaration);
        }

        return new Namespace(name, children);
    }

    public IDeclaration createTypeDeclaration(JavaTypeInfo info) {
        //Collect Parents
        Class<?> javaClazz = info.getJavaClazz();
        //parents
        List<TypeReference> parents = new ArrayList<>();
        Type superclass = javaClazz.getGenericSuperclass();
        TypeReference reference = createReference(superclass);
        if (reference != null) {
            parents.add(reference);
        }
        Type[] interfaces = javaClazz.getGenericInterfaces();
        for (Type anInterface : interfaces) {
            TypeReference interfaceRef = createReference(anInterface);
            if (interfaceRef != null) {
                parents.add(interfaceRef);
            }
        }
        String name = javaClazz.getSimpleName();

        Map<String, JavaInstanceMember> javaMembers = info.getMembers();
        TypeParameters typeParameters = createTypeParameters(javaClazz.getTypeParameters());

        List<Member> members = new ArrayList<>();
        if (javaMembers != null && !javaMembers.isEmpty()) {
            for (Map.Entry<String, JavaInstanceMember> entry : javaMembers.entrySet()) {
                Member member = createMember(entry.getKey(), entry.getValue());
                if (member != null) {
                    members.add(member);
                }
            }
        }

        if (members.isEmpty()) {
            if (parents.size() == 0) {
                return new TypeAliasDeclaration(name, PredefinedType.OBJECT, typeParameters);
            }
            if (parents.size() == 1) {
                return new TypeAliasDeclaration(name, parents.get(0), typeParameters);
            }
            IntersectionType unionType = new IntersectionType(parents);
            return new TypeAliasDeclaration(javaClazz.getSimpleName(), unionType, typeParameters);
        }


        InterfaceDeclaration interfaceDeclaration = new InterfaceDeclaration(name, members, typeParameters, parents);
        manager.forEachPlugin(it -> it.onInterfaceBuild(javaClazz, interfaceDeclaration, false));
        return interfaceDeclaration;
    }

    public InterfaceDeclaration createStaticTypeDeclaration(String namespaceName, JavaTypeInfo info) {

        String clazzName = info.getJavaClazz().getSimpleName();

        String name = null;

        List<Member> members = new ArrayList<>();
        if (!info.isEmpty()) {
            JavaConstructorMember constructorMember = info.getConstructorMember();
            if (constructorMember != null) {
                Member member = createMember(constructorMember, info.getJavaClazz());
                if (member != null) {
                    members.add(member);

                    name = context.getTypeScope().clazzNoConflict(namespaceName, clazzName + "Constructor");
                }
            }
        }

        if (name == null) {
            name = context.getTypeScope().clazzNoConflict(namespaceName, clazzName + "Static");
        }

        Map<String, JavaStaticMember> staticMembers = info.getStaticMembers();
        if (staticMembers != null) {
            for (Map.Entry<String, JavaStaticMember> entry : staticMembers.entrySet()) {
                Member member = createMember(entry.getKey(), entry.getValue());
                if (member != null) {
                    members.add(member);
                }
            }
        }

        InterfaceDeclaration interfaceDeclaration = new InterfaceDeclaration(name, members, null, null);
        manager.forEachPlugin(it -> it.onInterfaceBuild(info.getJavaClazz(), interfaceDeclaration, true));

        if (interfaceDeclaration.getMembers().isEmpty()) {
            return null;
        }
        return interfaceDeclaration;
    }

    public TypeReference createReference(Type type) {
        if (type instanceof Class<?> && !((Class<?>) type).isArray()) {
            Class<?> rawType = (Class<?>) type;
            if (!context.canReference(rawType)) {
                return null;
            }
            TypeArguments typeArguments = BuilderUtils.createEmptyTypeArguments(rawType.getTypeParameters().length);
            String namespace = buildContext.getNamespace(rawType);
            return new TypeReference(typeArguments, namespace, rawType.getSimpleName());
        }
        if (type instanceof ParameterizedType) {
            Class<?> rawType = (Class<?>) ((ParameterizedType) type).getRawType();
            if (!context.canReference(rawType)) {
                return null;
            }
            Type[] arguments = ((ParameterizedType) type).getActualTypeArguments();
            TypeArguments typeArguments = createTypeArguments(arguments);
            String namespace = buildContext.getNamespace(rawType);
            return new TypeReference(typeArguments, namespace, rawType.getSimpleName());
        }
        if (type != null) {
            logger.warn("Could not resolve type reference for {}", type);
        }
        return null;
    }

    public TsType createReferenceNonnull(Type type) {
        return createReferenceNonnull(type, false);
    }

    @Nonnull
    public TsType createReferenceNonnull(Type type, boolean useWrapper) {
        if (type instanceof Class<?>) {
            Class<?> rawType = (Class<?>) type;
            if (rawType.isArray()) {
                TsType component = createReferenceNonnull(((Class<?>) type).getComponentType());
                return new ArrayType(component);
            }
            TsType wrap = context.findWrap(rawType);
            if (wrap != null) {
                return wrap;
            }
        }
        if (type instanceof GenericArrayType) {
            return new ArrayType(createReferenceNonnull(((GenericArrayType) type).getGenericComponentType()));
        }
        if (type instanceof TypeVariable) {
            return new TsTypeVariable(((TypeVariable<?>) type).getName());
        }
        if (type instanceof WildcardType) {

            Type[] upperBounds = ((WildcardType) type).getUpperBounds();
            Set<TsType> boundTypes = new HashSet<>();
            for (Type bound : upperBounds) {
                if (bound != Object.class) {
                    TsType reference = createReferenceNonnull(bound);
                    boundTypes.add(reference);
                }
            }
            if (boundTypes.size() == 0) {
                return PredefinedType.ANY;
            }
            if (boundTypes.size() == 1) {
                return boundTypes.stream().findAny().get();
            }
            return new IntersectionType(new ArrayList<>(boundTypes));
        }

        TypeReference reference = createReference(type);
        if (reference == null) {
            return PredefinedType.ANY;
        }


        if (useWrapper) {
            TsType wrapperReference = createWrapperReference(type, reference);
            if (wrapperReference != null) {
                return wrapperReference;
            }
        }

        return reference;
    }

    public TsType createWrapperReference(Type type, TypeReference old) {
        Class<?> clazz = null;
        if (type instanceof ParameterizedType) {
            clazz = (Class<?>) ((ParameterizedType) type).getRawType();
        } else if (type instanceof Class<?>) {
            clazz = (Class<?>) type;
        }
        if (clazz != null) {
            Class<?> finalClazz = clazz;
            TsType result = manager.applyOnPlugin(p -> p.onParameterWrapper(finalClazz, old));
            if (result != old) {
                return result;
            }
        }
        return null;
    }


    public TypeReference createReferenceNonnull(Class<?> clazz, TypeVariable<?>[] extraVariables) {
        if (!context.isResolved(clazz)) {
            return null;
        }
        TypeArguments typeArguments = createTypeArguments(extraVariables);
        String namespace = buildContext.getNamespace(clazz);
        return new TypeReference(typeArguments, namespace, clazz.getSimpleName());
    }

    public Member createMember(String name, JavaInstanceMember instanceMember) {
        List<MethodSignature> methods = instanceMember.getMethods();
        PropertySignature field = instanceMember.getField();
        List<CallSignature> callSignatures = null;
        if (methods != null && !methods.isEmpty()) {
            callSignatures = new ArrayList<>(methods.size());
            for (MethodSignature method : methods) {
                CallSignature callSignature = createCallSignature(method);
                callSignatures.add(callSignature);
            }
            callSignatures.sort(MethodTypeUtils::compare);
        }

        if (field != null && callSignatures != null) {
            return new FieldAndMethodMember(name, field.isReadonly(), createReferenceNonnull(field.getType()), callSignatures);
        } else if (field != null) {
            return new FieldMember(name, field.isReadonly(), createReferenceNonnull(field.getType()));
        } else if (callSignatures != null) {
            return new MethodMember(name, callSignatures);
        }

        return null;
    }

    public Member createMember(String name, JavaStaticMember staticMember) {
        List<Method> methods = staticMember.getMethods();
        Field field = staticMember.getField();
        PropertySignature possibleField;
        if (field != null) {
            possibleField = new PropertySignature(name, field);
        } else {
            possibleField = staticMember.getBean();
        }

        List<CallSignature> callSignatures = null;
        if (methods != null && !methods.isEmpty()) {
            callSignatures = new ArrayList<>(methods.size());
            for (Method method : methods) {
                CallSignature callSignature = createCallSignature(method);
                callSignatures.add(callSignature);
            }
            callSignatures.sort(MethodTypeUtils::compare);
        }
        if (possibleField != null && callSignatures != null) {
            TsType fieldType = createReferenceNonnull(possibleField.getType());
            return new FieldAndMethodMember(name, possibleField.isReadonly(), fieldType, callSignatures);
        } else if (field != null) {
            return new FieldMember(name, possibleField.isReadonly(), createReferenceNonnull(possibleField.getType()));
        } else if (callSignatures != null) {
            return new MethodMember(name, callSignatures);
        }

        return null;
    }

    public Member createMember(JavaConstructorMember constructorMember, Class<?> clazz) {
        List<Constructor<?>> constructors = constructorMember.getConstructors();

        List<TsConstructorSignature> signatures = new ArrayList<>(constructors.size());
        for (Constructor<?> constructor : constructors) {
            TsConstructorSignature signature = createConstructorSignature(constructor, clazz);
            signatures.add(signature);
        }

        return new ConstructorMember(signatures);
    }

    public TypeParameters createTypeParameters(TypeVariable<?>[] variables) {
        if (variables == null || variables.length == 0) {
            return null;
        }
        List<TypeParameter> parameters = new ArrayList<>(variables.length);
        for (TypeVariable<?> variable : variables) {
            Type[] bounds = variable.getBounds();
            Set<TsType> boundTypes = new HashSet<>();
            for (Type bound : bounds) {
                TsType reference = createReferenceNonnull(bound);
                if (reference instanceof TypeReference) {
                    boundTypes.add(reference);
                }
                if (reference == PredefinedType.STRING || reference == PredefinedType.NUMBER || reference == PredefinedType.BOOLEAN) {
                    boundTypes.add(reference);
                }
            }
            ArrayList<TsType> boundTypesList = new ArrayList<>(boundTypes);
            if (boundTypes.size() == 0) {
                TypeParameter typeParameter = new TypeParameter(variable.getName());
                parameters.add(typeParameter);
            } else if (boundTypes.size() == 1) {
                TypeParameter typeParameter = new TypeParameter(variable.getName(), boundTypesList.get(0));
                parameters.add(typeParameter);
            } else {
                TypeParameter typeParameter = new TypeParameter(variable.getName(), new IntersectionType(boundTypesList));
                parameters.add(typeParameter);
            }
        }

        return new TypeParameters(parameters);
    }


    public CallSignature createCallSignature(MethodSignature method) {
        TypeVariable<?>[] variables = method.getVariables();
        TypeParameters typeParameters = createTypeParameters(variables);
        TsType returnType = createReferenceNonnull(method.getReturnType());
        List<TsType> parameterTypes = createParameterTypes(method.getParameterType());
        List<String> parameterNames = parameterNameResolver.find(method.getRawMethod());
        CallSignature callSignature = new CallSignature(parameterTypes, typeParameters, returnType, parameterNames);
        callSignature.setVarargs(method.getRawMethod().isVarArgs());
        return callSignature;
    }


    private List<TsType> createParameterTypes(Type[] parameterTypes) {
        List<TsType> paramsTypes = new ArrayList<>(parameterTypes.length);
        for (Type type : parameterTypes) {
            paramsTypes.add(createReferenceNonnull(type, true));
        }
        return paramsTypes;
    }

    public CallSignature createCallSignature(Method method) {
        TypeVariable<?>[] variables = method.getTypeParameters();
        TypeParameters typeParameters = createTypeParameters(variables);
        TsType returnType = createReferenceNonnull(method.getGenericReturnType());
        List<TsType> parameterTypes = createParameterTypes(method.getGenericParameterTypes());
        List<String> parameterNames = parameterNameResolver.find(method);
        CallSignature callSignature = new CallSignature(parameterTypes, typeParameters, returnType, parameterNames);
        callSignature.setVarargs(method.isVarArgs());
        return callSignature;
    }

    public TsConstructorSignature createConstructorSignature(Constructor<?> constructor, Class<?> clazz) {
        TypeVariable<?>[] clazzVariables = clazz.getTypeParameters();
        TsType returnType = createReferenceNonnull(clazz, clazzVariables);
        if (returnType == null) {
            return null;
        }
        TypeVariable<?>[] constructorTypeParameters = constructor.getTypeParameters();
        clazzVariables = Stream.of(clazzVariables, constructorTypeParameters).flatMap(Arrays::stream).toArray(TypeVariable[]::new);
        TypeParameters typeParameters = createTypeParameters(clazzVariables);
        List<TsType> paramsTypes = createParameterTypes(constructor.getGenericParameterTypes());

        List<String> parameterNames = parameterNameResolver.find(constructor);
        return new TsConstructorSignature(paramsTypes, typeParameters, returnType, parameterNames);
    }


    public TypeArguments createTypeArguments(Type[] arguments) {
        if (arguments.length == 0) {
            return null;
        }
        ArrayList<TsType> list = new ArrayList<>(arguments.length);
        for (Type argument : arguments) {
            TsType referenceNonnull = createReferenceNonnull(argument);
            list.add(referenceNonnull);
        }
        return new TypeArguments(list);
    }


}
