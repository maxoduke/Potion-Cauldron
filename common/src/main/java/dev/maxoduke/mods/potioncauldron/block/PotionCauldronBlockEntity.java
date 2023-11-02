package dev.maxoduke.mods.potioncauldron.block;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PotionCauldronBlockEntity extends BlockEntity
{
    private Potion potion;
    private String potionType;

    public PotionCauldronBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(PotionCauldron.BLOCK_ENTITY, blockPos, blockState);

        potion = Potions.EMPTY;
        potionType = BuiltInRegistries.ITEM.getKey(Items.POTION).toString();
    }

    public @NotNull Potion getPotion() { return potion; }

    public void setPotion(Potion potion) { this.potion = potion; }

    public @NotNull String getPotionType() { return potionType; }

    public void setPotionType(String potionType) { this.potionType = potionType; }

    @Override
    public void load(CompoundTag tag)
    {
        potion = BuiltInRegistries.POTION.get(ResourceLocation.tryParse(tag.getString("PotionName")));
        potionType = tag.getString("PotionType");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag)
    {
        if (potion == null)
            return;

        ResourceLocation potionResource = BuiltInRegistries.POTION.getKey(potion);
        String potionName = potionResource.toString();

        tag.putString("PotionName", potionName);
        tag.putString("PotionType", potionType);
    }

    @Override
    @Nullable
    public Packet<ClientGamePacketListener> getUpdatePacket()
    {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    @NotNull
    public CompoundTag getUpdateTag()
    {
        return saveWithFullMetadata();
    }
}
