package dev.maxoduke.mods.potioncauldron.config;

public class ClientConfig implements IConfig
{
    private final boolean evaporatePotionWhenMixed;
    private final boolean allowMergingPotions;
    private final boolean allowCreatingTippedArrows;

    public ClientConfig(boolean evaporatePotionWhenMixed, boolean allowMergingPotions, boolean allowCreatingTippedArrows)
    {
        this.evaporatePotionWhenMixed = evaporatePotionWhenMixed;
        this.allowMergingPotions = allowMergingPotions;
        this.allowCreatingTippedArrows = allowCreatingTippedArrows;
    }

    public boolean shouldEvaporatePotionWhenMixed() { return evaporatePotionWhenMixed; }

    public boolean shouldAllowMergingPotions() { return allowMergingPotions; }

    public boolean shouldAllowCreatingTippedArrows() { return allowCreatingTippedArrows; }
}
