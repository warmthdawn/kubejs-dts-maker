package com.warmthdawn.mod.kubejsdtsmaker.builder;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.warmthdawn.mod.kubejsdtsmaker.context.BuildContext;
import com.warmthdawn.mod.kubejsdtsmaker.context.GlobalTypeScope;
import com.warmthdawn.mod.kubejsdtsmaker.context.ResolveContext;
import com.warmthdawn.mod.kubejsdtsmaker.java.JavaConstructorMember;
import com.warmthdawn.mod.kubejsdtsmaker.java.JavaInstanceMember;
import com.warmthdawn.mod.kubejsdtsmaker.java.JavaStaticMember;
import com.warmthdawn.mod.kubejsdtsmaker.java.JavaTypeInfo;
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
import com.warmthdawn.mod.kubejsdtsmaker.util.MethodSignature;
import com.warmthdawn.mod.kubejsdtsmaker.util.MiscUtils;
import com.warmthdawn.mod.kubejsdtsmaker.util.PropertySignature;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.CallbackI;

import javax.annotation.Nonnull;
import java.lang.reflect.*;
import java.util.*;

public class TsTreeFactory {
    private final ResolveContext context;
    private final BuildContext buildContext;
    private static final Logger logger = LogManager.getLogger();

    public TsTreeFactory(ResolveContext context) {
        this.context = context;
        this.buildContext = new BuildContext();
    }

    public DeclarationFile createFile() {
        GlobalTypeScope typeScope = context.getTypeScope();
        Map<Class<?>, JavaTypeInfo> allTypes = context.getTypeInfos();

        Multimap<String, Class<?>> groupedTypes = TreeMultimap.create(Comparator.naturalOrder(), Comparator.comparing(Class::getSimpleName));

        //吧所有类按命名空间分组
        for (Class<?> clazz : allTypes.keySet()) {
            String namespace = MiscUtils.getNamespace(clazz);
            String namespaceName = typeScope.namespaceNoConflict(namespace);
            groupedTypes.put(namespaceName, clazz);
            buildContext.addNamespace(clazz, namespaceName);
        }
        List<Namespace> namespaces = new ArrayList<>(groupedTypes.keySet().size());
        for (String namespaceName : groupedTypes.keySet()) {
            Namespace namespace = createNamespace(namespaceName, groupedTypes.get(namespaceName));
            namespaces.add(namespace);
        }
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

            IDeclaration staticDeclaration = createStaticTypeDeclaration(name, typeInfo);
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

        if (javaMembers == null || javaMembers.isEmpty()) {
            if (parents.size() == 0) {
                return new TypeAliasDeclaration(name, PredefinedTypes.OBJECT, typeParameters);
            }
            if (parents.size() == 1) {
                return new TypeAliasDeclaration(name, parents.get(0), typeParameters);
            }
            UnionType unionType = new UnionType(parents);
            return new TypeAliasDeclaration(javaClazz.getSimpleName(), unionType, typeParameters);
        }

        List<Member> members = new ArrayList<>(javaMembers.size());
        for (Map.Entry<String, JavaInstanceMember> entry : javaMembers.entrySet()) {
            Member member = createMember(entry.getKey(), entry.getValue());
            if (member != null) {
                members.add(member);
            }
        }

        return new InterfaceDeclaration(name, members, typeParameters, parents);
    }

    public IDeclaration createStaticTypeDeclaration(String namespaceName, JavaTypeInfo info) {

        String name = info.getJavaClazz().getSimpleName();

        name = context.getTypeScope().clazzNoConflict(namespaceName, name + "Constructor");

        List<Member> members = new ArrayList<>();
        JavaConstructorMember constructorMember = info.getConstructorMember();
        if (constructorMember != null) {
            Member member = createMember(constructorMember, info.getJavaClazz());
            if (member != null) {
                members.add(member);
            }
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
        if (members.isEmpty()) {
            return null;
        }

        return new InterfaceDeclaration(name, members, null, null);
    }

    public TypeReference createReference(Type type) {
        if (type instanceof Class<?> && !((Class<?>) type).isArray()) {
            Class<?> rawType = (Class<?>) type;
            if (!context.canReference(rawType)) {
                return null;
            }
            TypeArguments typeArguments = createTypeArguments(rawType.getTypeParameters().length);
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

    @Nonnull
    public TsType createReferenceNonnull(Type type) {
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
                TsType reference = createReferenceNonnull(bound);
                boundTypes.add(reference);
            }
            if (boundTypes.size() == 0) {
                return PredefinedTypes.ANY;
            }
            if (boundTypes.size() == 1) {
                return boundTypes.stream().findAny().get();
            }
            return new IntersectionType(new ArrayList<>(boundTypes));
        }

        TypeReference reference = createReference(type);
        if (reference == null) {
            return PredefinedTypes.ANY;
        }
        return reference;
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
                if (reference == PredefinedTypes.STRING || reference == PredefinedTypes.NUMBER || reference == PredefinedTypes.BOOLEAN) {
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
        List<TsType> paramsTypes = new ArrayList<>(method.getParameterType().length);
        for (Type type : method.getParameterType()) {
            paramsTypes.add(createReferenceNonnull(type));
        }
        return new CallSignature(paramsTypes, typeParameters, returnType);
    }


    public CallSignature createCallSignature(Method method) {
        TypeVariable<?>[] variables = method.getTypeParameters();
        TypeParameters typeParameters = createTypeParameters(variables);
        TsType returnType = createReferenceNonnull(method.getGenericReturnType());
        List<TsType> paramsTypes = new ArrayList<>(method.getGenericParameterTypes().length);
        for (Type type : method.getGenericParameterTypes()) {
            paramsTypes.add(createReferenceNonnull(type));
        }
        return new CallSignature(paramsTypes, typeParameters, returnType);
    }

    public TsConstructorSignature createConstructorSignature(Constructor<?> constructor, Class<?> clazz) {
        TypeVariable<?>[] clazzVariables = clazz.getTypeParameters();
        TsType returnType = createReferenceNonnull(clazz, clazzVariables);
        if (returnType == null) {
            return null;
        }
        TypeParameters typeParameters = createTypeParameters(clazzVariables);
        List<TsType> paramsTypes = new ArrayList<>(constructor.getGenericParameterTypes().length);
        for (Type type : constructor.getGenericParameterTypes()) {
            paramsTypes.add(createReferenceNonnull(type));
        }
        return new TsConstructorSignature(paramsTypes, typeParameters, returnType);
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

    public TypeArguments createTypeArguments(int argCount) {
        if (argCount == 0) {
            return null;
        }
        ArrayList<TsType> list = new ArrayList<>(argCount);
        for (int i = 0; i < argCount; i++) {
            list.add(PredefinedTypes.ANY);
        }
        return new TypeArguments(list);
    }
}
