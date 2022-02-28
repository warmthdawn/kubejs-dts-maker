package com.warmthdawn.mod.kubejsgrammardump.command;

import com.mojang.brigadier.CommandDispatcher;
import com.warmthdawn.mod.kubejsdtsmaker.BuilderManager;
import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.builder.GlobalMemberFactory;
import com.warmthdawn.mod.kubejsdtsmaker.builder.TypescriptFactory;
import com.warmthdawn.mod.kubejsdtsmaker.bytecode.BytecodeUtils;
import com.warmthdawn.mod.kubejsdtsmaker.bytecode.ScanResult;
import com.warmthdawn.mod.kubejsdtsmaker.collector.WrappedBindingsEvent;
import com.warmthdawn.mod.kubejsdtsmaker.context.KubeJsGlobalContext;
import com.warmthdawn.mod.kubejsdtsmaker.context.ResolveContext;
import com.warmthdawn.mod.kubejsdtsmaker.resolver.JavaClassResolver;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.DeclarationFile;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.global.IGlobalDeclaration;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

public class DumpCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("dump_kjs").requires(
            it -> it.hasPermission(2)
        ).executes((it) -> {
            CommandSource commandsource = it.getSource();
            try {
//                DeclarationBuilder.build();

                BuilderManager builderManager = BuilderManager.create();
                builderManager.resolveClasses();
                String s = builderManager.generateResult();
                try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get("minecraft.d.ts"), StandardCharsets.UTF_8)) {
                    bufferedWriter.write(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("build successful");
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return 0;
        }));


        dispatcher.register(Commands.literal("dump_events").requires(
            it -> it.hasPermission(2)
        ).executes((it) -> {
            CommandSource commandsource = it.getSource();
            try {
                ScanResult result = BytecodeUtils.scanAllMods();
                System.out.println(result);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return 0;
        }));
    }
}
