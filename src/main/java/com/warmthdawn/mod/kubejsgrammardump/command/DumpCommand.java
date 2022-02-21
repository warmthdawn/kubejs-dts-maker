package com.warmthdawn.mod.kubejsgrammardump.command;

import com.mojang.brigadier.CommandDispatcher;
import com.warmthdawn.mod.kubejsgrammardump.collector.JavaClassCollector;
import com.warmthdawn.mod.kubejsgrammardump.typescript.DeclarationBuilder;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class DumpCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("dump_kjs").requires(
            it -> it.hasPermission(2)
        ).executes((it) -> {
            CommandSource commandsource = it.getSource();
//            try {
//                DeclarationBuilder.build();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            return 0;
        }));
    }
}
