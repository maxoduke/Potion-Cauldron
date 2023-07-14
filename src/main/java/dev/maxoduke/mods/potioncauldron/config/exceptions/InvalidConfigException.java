package dev.maxoduke.mods.potioncauldron.config.exceptions;

public class InvalidConfigException extends RuntimeException
{
    public InvalidConfigException()
    {
        super("Invalid potion cauldron config found, creating a new one...");
    }
}
