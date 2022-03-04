package com.warmthdawn.mod.kubejsdtsmaker;

import com.warmthdawn.mod.kubejsdtsmaker.command.DumpCommand;
import com.warmthdawn.mod.kubejsdtsmaker.special.template.ExternalDeclarationsManager;
import com.warmthdawn.mod.kubejsdtsmaker.wrappers.BuiltinWrappers;
import dev.latvian.kubejs.script.ScriptManager;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("kubejsdtsmaker")
public class KubeJSDtsMaker {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public KubeJSDtsMaker() {

        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(this::preInit);
    }

    private void preInit(FMLCommonSetupEvent t) {

        BuiltinWrappers.addDefaultWrappers();
        ExternalDeclarationsManager.getInstance().load();

    }


    public void registerCommands(RegisterCommandsEvent event) {
        DumpCommand.register(event.getDispatcher());
    }
}
