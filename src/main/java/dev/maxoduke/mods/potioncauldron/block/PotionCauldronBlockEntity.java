package dev.maxoduke.mods.potioncauldron.block;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
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

import java.util.Optional;

public class PotionCauldronBlockEntity extends BlockEntity
{
    private Holder<Potion> potion;
    private String potionType;

    public PotionCauldronBlockEntity(BlockPos blockPos, BlockState blockState)
    {
        super(PotionCauldron.BLOCK_ENTITY, blockPos, blockState);

        potion = Potions.FIRE_RESISTANCE;
        potionType = BuiltInRegistries.ITEM.getKey(Items.POTION).toString();
    }

    public @Nullable Holder<Potion> getPotion() { return potion; }

    public void setPotion(Holder<Potion> potion) { this.potion = potion; }

    public @NotNull String getPotionType() { return potionType; }

    public void setPotionType(String potionType) { this.potionType = potionType; }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.@NotNull Provider provider)
    {
        ResourceLocation potionNameResourceLocation = ResourceLocation.tryParse(tag.getString("PotionName"));
        if (potionNameResourceLocation == null)
            return;

        Optional<Holder.Reference<Potion>> holder = BuiltInRegistries.POTION.getHolder(potionNameResourceLocation);
        if (holder.isEmpty())
            return;

        potion = holder.get();
        potionType = tag.getString("PotionType");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider)
    {
        if (potion == null)
            return;

        ResourceLocation potionResource = BuiltInRegistries.POTION.getKey(potion.value());
        if (potionResource == null)
            return;

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
    public CompoundTag getUpdateTag(HolderLookup.@NotNull Provider provider)
    {
        return saveWithFullMetadata(provider);
    }
}
