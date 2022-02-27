package com.warmthdawn.mod.kubejsdtsmaker.typescript.member;

import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.misc.CallSignature;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.types.TsType;

import java.util.List;

public class FieldAndMethodMember implements Member {
    private String name;
    private boolean fieldReadonly;
    private TsType fieldType;
    private List<CallSignature> methods;

    public FieldAndMethodMember(String name, boolean fieldReadonly, TsType fieldType, List<CallSignature> methods) {
        this.name = name;
        this.fieldReadonly = fieldReadonly;
        this.fieldType = fieldType;
        this.methods = methods;
    }

    @Override
    public void build(DeclarationBuilder builder) {
        if (fieldReadonly) {
            //Output Example:
            //readonly test: {
            //    (val: number): string
            //    (val: string): void
            //} & number
            builder.newLine()
                .append("readonly ")
                .append(name)
                .append(": ");
            buildMethodList(builder);
            builder.append(" & ")
                .append(fieldType)
                .append(";");
        } else {
            //Output Example:
            //get test(): {
            //    (val: number): string
            //    (val: string): void
            //} & number
            //set test(val: number)
            builder.newLine()
                .append("get ")
                .append(name)
                .append("(): ");
            buildMethodList(builder);
            builder.append(" & ")
                .append(fieldType)
                .append(";")
                .newLine()
                .append("set ")
                .append(name)
                .append("(val: ")
                .append(fieldType)
                .append(");");
        }
    }

    private void buildMethodList(DeclarationBuilder builder) {
        if (methods.size() == 1) {
            //Output Example:
            //(val: number) => string
            methods.get(0).buildType(builder);
        } else {
            //Output Example:
            //{
            //    (val: number): string
            //    (val: string): void
            //}
            builder.append("{")
                .increaseIndent();
            for (CallSignature method : methods) {
                builder.newLine()
                    .append(method)
                    .append(";");
            }
            builder.decreaseIndent()
                .newLine()
                .append("}");
        }
    }

    @Override
    public String getName() {
        return name;
    }


    @Override
    public String toString() {
        return name + "() & " + name;
    }
}
