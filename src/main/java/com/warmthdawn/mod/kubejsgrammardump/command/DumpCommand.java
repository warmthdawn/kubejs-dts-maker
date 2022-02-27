package com.warmthdawn.mod.kubejsgrammardump.command;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.CommandDispatcher;
import com.warmthdawn.mod.kubejsdtsmaker.builder.DeclarationBuilder;
import com.warmthdawn.mod.kubejsdtsmaker.builder.TsTreeFactory;
import com.warmthdawn.mod.kubejsdtsmaker.context.ResolveContext;
import com.warmthdawn.mod.kubejsdtsmaker.resolver.JavaClassResolver;
import com.warmthdawn.mod.kubejsdtsmaker.typescript.DeclarationFile;
import com.warmthdawn.mod.kubejsgrammardump.collector.JavaClassCollector;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.ChestEventJS;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class DumpCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("dump_kjs").requires(
            it -> it.hasPermission(2)
        ).executes((it) -> {
            CommandSource commandsource = it.getSource();
            try {
//                DeclarationBuilder.build();
                ResolveContext context = new ResolveContext();
                JavaClassResolver.resolve(Collections.singletonList(ChestEventJS.class), context, 2);
//                JavaClassResolver.resolve(Collections.singletonList(ImmutableList.class), context, 0);
                TsTreeFactory tsTreeFactory = new TsTreeFactory(context);
                DeclarationFile file = tsTreeFactory.createFile();
                DeclarationBuilder declarationBuilder = new DeclarationBuilder();
                file.build(declarationBuilder);
                try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get("minecraft.d.ts"), StandardCharsets.UTF_8)) {
                    bufferedWriter.write(declarationBuilder.build());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(context);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return 0;
        }));
    }
}
