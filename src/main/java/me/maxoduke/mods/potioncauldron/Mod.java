package me.maxoduke.mods.potioncauldron;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Mod implements ModInitializer {
    public static final String MOD_ID = "potioncauldron";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Potion Cauldron initialized!");
    }
}
