package com.warmthdawn.mod.kubejsdtsmaker.plugins;

import com.warmthdawn.mod.kubejsdtsmaker.BuilderManager;
import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.builder.TypescriptFactory;
import com.warmthdawn.mod.kubejsdtsmaker.context.ResolveContext;
import com.warmthdawn.mod.kubejsdtsmaker.java.JavaTypeInfo;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.declaration.InterfaceDeclaration;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.generic.TypeParameters;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.member.*;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.misc.CallSignature;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.PredefinedType;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsTypeVariable;
import dev.latvian.mods.rhino.SymbolKey;
import dev.latvian.mods.rhino.util.ListLike;
import dev.latvian.mods.rhino.util.MapLike;

import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.warmthdawn.mod.kubejsdtsmaker.util.InterfaceMemberUtils.removeMember;

public class RhinoExtrasPlugin implements IBuilderPlugin {
    private TypescriptFactory typescriptFactory;
    private ResolveContext context;

    @Override
    public void init(BuilderManager manager) {
        this.typescriptFactory = manager.getTypescriptFactory();
        this.context = manager.getResolveContext();
    }

    @Override
    public void onInterfaceBuild(Class<?> javaClazz, InterfaceDeclaration interfaceDeclaration, boolean isStatic) {
        List<Member> members = interfaceDeclaration.getMembers();
        if (isStatic) {
            JavaTypeInfo typeInfo = context.get(javaClazz);
            if (typeInfo != null && !typeInfo.isEmpty()) {
                CallSignature forceCastMember = createForceCastMember(javaClazz);
                if (forceCastMember != null) {
                    members.add(new Member() {
                        @Override
                        public String getName() {
                            return "()";
                        }

                        @Override
                        public void build(DeclarationBuilder builder) {
                            builder.newLine();
                            forceCastMember.build(builder);
                        }
                    });
                }
            }
            return;
        }

        if (Map.class == javaClazz || MapLike.class == javaClazz) {
            //对不起这里不支持泛型
            members.add(new IndexMember("key", true, false, PredefinedType.ANY));
        }

        if (List.class == javaClazz || ListLike.class == javaClazz) {
            members.add(new FieldMember("length", true, PredefinedType.NUMBER));

            members.add(new FieldMember("[" + SymbolKey.IS_CONCAT_SPREADABLE.getName() + "]", true, PredefinedType.BOOLEAN));

            TypeVariable<?>[] typeParameters = javaClazz.getTypeParameters();
            TsTypeVariable typeVariable = new TsTypeVariable(typeParameters[0].getName());
            members.add(new IndexMember("key", false, false, typeVariable));
        } else if (List.class.isAssignableFrom(javaClazz) || ListLike.class.isAssignableFrom(javaClazz)) {
            removeMember(members, "length");
        }


    }


    public CallSignature createForceCastMember(Class<?> clazz) {
        TypeVariable<?>[] clazzVariables = clazz.getTypeParameters();
        TypeParameters typeParameters = typescriptFactory.createTypeParameters(clazzVariables);
        TsType returnType = typescriptFactory.createReferenceNonnull(clazz, clazzVariables);
        return new CallSignature(Collections.singletonList(PredefinedType.ANY), typeParameters, returnType,
            Collections.singletonList("val"));
    }


}
