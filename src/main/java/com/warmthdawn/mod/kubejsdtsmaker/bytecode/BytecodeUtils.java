package com.warmthdawn.mod.kubejsdtsmaker.bytecode;

import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;

import java.util.List;

public class BytecodeUtils {
    public static ScanResult scanAllMods() {
        ScanResult result = new ScanResult();
        List<ModFileInfo> modFiles = ModList.get().getModFiles();
        for (ModFileInfo modFile : modFiles) {
            new ModFileScanner(modFile.getFile()).scan(result);
        }
        return result;
    }

}
