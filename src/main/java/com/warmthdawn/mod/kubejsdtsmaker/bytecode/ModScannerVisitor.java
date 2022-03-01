package com.warmthdawn.mod.kubejsdtsmaker.bytecode;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ModScannerVisitor extends ClassVisitor {
    private final ScanResult result;

    public ModScannerVisitor(ScanResult result) {
        super(Opcodes.ASM7);
        this.result = result;
    }

    private String clazzName = null;

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.clazzName = name;
        result.addClassInfo(name, superName);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        this.clazzName = null;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return new ModScannerNode(access, name, descriptor, signature, exceptions, result, clazzName);
    }


    public static class ModScannerNode extends MethodNode {
        private final ScanResult result;
        private final String clazzName;

        public ModScannerNode(int access, String name, String desc, String signature, String[] exceptions, ScanResult result, String clazzName) {
            super(Opcodes.ASM7, access, name, desc, signature, exceptions);
            this.result = result;
            this.clazzName = clazzName;
        }

        @Override
        public void visitEnd() {
            super.visitEnd();
            //scan method parameter names

            if (parameters != null && clazzName != null && parameters.size() > 0) {
                List<String> parameterNames = new ArrayList<>(parameters.size());
                for (ParameterNode parameter : parameters) {
                    parameterNames.add(parameter.name);
                }
                result.addMethodParametersInfo(name, clazzName, desc, parameterNames);
            }

            //scan event post
            for (AbstractInsnNode instruction : instructions) {

                if (instruction instanceof MethodInsnNode && instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                    MethodInsnNode node = (MethodInsnNode) instruction;
                    if (!Objects.equals(node.name, "post") ||
                        !Objects.equals(node.desc, "(Ldev/latvian/kubejs/script/ScriptType;Ljava/lang/String;)Z")) {
                        continue;
                    }

                    AbstractInsnNode previous = node.getPrevious();
                    if (previous instanceof LdcInsnNode) {
                        Object cst = ((LdcInsnNode) previous).cst;
                        if (cst instanceof String) {
                            String owner = node.owner;
                            result.addEventPostInfo(owner, (String) cst);
                        }
                    }
                }
            }
        }
    }
}
