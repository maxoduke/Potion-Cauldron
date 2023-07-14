package dev.maxoduke.mods.potioncauldron;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings("SpellCheckingInspection")
public class Mod implements ModInitializer, ClientModInitializer
{
    public static final String ID = "potioncauldron";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    @Override
    public void onInitialize()
    {
        PotionCauldron.initialize();
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void onInitializeClient()
    {
        PotionCauldron.initializeClient();
    }
}
