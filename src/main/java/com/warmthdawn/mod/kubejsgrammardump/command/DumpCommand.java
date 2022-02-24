package com.warmthdawn.mod.kubejsgrammardump.command;

import com.mojang.brigadier.CommandDispatcher;
import com.warmthdawn.mod.kubejsdtsmaker.context.ResolveContext;
import com.warmthdawn.mod.kubejsdtsmaker.resolver.JavaClassResolver;
import com.warmthdawn.mod.kubejsgrammardump.collector.JavaClassCollector;
import com.warmthdawn.mod.kubejsgrammardump.typescript.DeclarationBuilder;
import dev.latvian.kubejs.entity.EntityJS;
import dev.latvian.kubejs.player.ChestEventJS;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

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
                System.out.println(context);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return 0;
        }));
    }
}
