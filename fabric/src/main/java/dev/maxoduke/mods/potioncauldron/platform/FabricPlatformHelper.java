package dev.maxoduke.mods.potioncauldron.platform;

import dev.maxoduke.mods.potioncauldron.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;

public class FabricPlatformHelper implements IPlatformHelper
{
    @Override
    public String getConfigDirPath()
    {
        return FabricLoader.getInstance().getConfigDir().toString();
    }
}
