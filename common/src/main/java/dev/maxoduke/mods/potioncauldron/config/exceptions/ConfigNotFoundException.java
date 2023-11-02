package dev.maxoduke.mods.potioncauldron.config.exceptions;

public class ConfigNotFoundException extends RuntimeException
{
    public ConfigNotFoundException()
    {
        super("Potion cauldron config not found! Creating a default one...");
    }
}
