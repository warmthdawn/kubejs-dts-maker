package com.warmthdawn.mod.kubejsdtsmaker.bytecode;

import net.minecraftforge.fml.loading.LogMarkers;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModFileScanner {

    private static final Logger LOGGER = LogManager.getLogger();
    private final ModFile fileToScan;

    public ModFileScanner(ModFile fileToScan) {
        this.fileToScan = fileToScan;
    }


    public ScanResult scan(ScanResult result) {
        this.fileToScan.scanFile((p) -> {
            this.fileVisitor(p, result);
        });

        return result;

    }

    public ScanResult scan() {
        ScanResult result = new ScanResult();
        return scan(result);

    }

    private void fileVisitor(Path path, ScanResult result) {
        LOGGER.debug(LogMarkers.SCAN, "Scanning {} path {}", this.fileToScan, path);

        try {
            InputStream in = Files.newInputStream(path);
            Throwable var4 = null;

            try {
                ClassReader cr = new ClassReader(in);
                cr.accept(new ModScannerVisitor(result), 0);

            } catch (Throwable var15) {
                var4 = var15;
                throw var15;
            } finally {
                if (in != null) {
                    if (var4 != null) {
                        try {
                            in.close();
                        } catch (Throwable var14) {
                            var4.addSuppressed(var14);
                        }
                    } else {
                        in.close();
                    }
                }

            }
        } catch (IllegalArgumentException | IOException var17) {
        }

    }
}
