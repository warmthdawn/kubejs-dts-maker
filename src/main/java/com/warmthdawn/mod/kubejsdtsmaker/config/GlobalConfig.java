package com.warmthdawn.mod.kubejsdtsmaker.config;


public class GlobalConfig {
    private GlobalConfig() {}
    public static final GlobalConfig INSTANCE = new GlobalConfig();

    private boolean enableDetailOnSelf = false;


    public boolean isEnableDetailOnSelf() {
        return enableDetailOnSelf;
    }

    public void setEnableDetailOnSelf(boolean enableOnSelf) {
        this.enableDetailOnSelf = enableOnSelf;
    }
}
