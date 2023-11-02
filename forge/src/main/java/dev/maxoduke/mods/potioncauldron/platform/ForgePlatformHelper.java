package dev.maxoduke.mods.potioncauldron.platform;

import dev.maxoduke.mods.potioncauldron.platform.services.IPlatformHelper;
import net.minecraftforge.fml.loading.FMLPaths;

public class ForgePlatformHelper implements IPlatformHelper
{
    @Override
    public String getConfigDirPath()
    {
        return FMLPaths.CONFIGDIR.get().toString();
    }
}