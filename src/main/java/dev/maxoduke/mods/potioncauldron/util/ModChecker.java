package dev.maxoduke.mods.potioncauldron.util;

import net.fabricmc.loader.api.FabricLoader;

public class ModChecker
{
    public static boolean isSodiumPresent()
    {
        return FabricLoader.getInstance().isModLoaded("sodium");
    }
}
