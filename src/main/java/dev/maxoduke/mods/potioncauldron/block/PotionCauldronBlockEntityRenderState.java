package dev.maxoduke.mods.potioncauldron.block;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Holder;
import net.minecraft.world.item.alchemy.Potion;

public class PotionCauldronBlockEntityRenderState extends BlockEntityRenderState
{
    private int liquidLevel = 0;
    private Holder<Potion> potion = null;
    private int spriteColor = 0;

    public Holder<Potion> getPotion()
    {
        return potion;
    }

    public void setPotion(Holder<Potion> potion)
    {
        this.potion = potion;
    }

    public int getLiquidLevel()
    {
        return liquidLevel;
    }

    public void setLiquidLevel(int liquidLevel)
    {
        this.liquidLevel = liquidLevel;
    }

    public int getSpriteColor()
    {
        return spriteColor;
    }

    public void setSpriteColor(int spriteColor)
    {
        this.spriteColor = spriteColor;
    }
}
