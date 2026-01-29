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
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
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
    public void loadAdditional(ValueInput valueInput)
    {
        Optional<String> optionalOptionNameIdentifier = valueInput.getString("PotionName");
        Optional<String> optionalPotionType = valueInput.getString("PotionType");
        if (optionalOptionNameIdentifier.isEmpty() || optionalPotionType.isEmpty())
            return;

        Identifier potionNameIdentifier = Identifier.tryParse(optionalOptionNameIdentifier.get());
        if (potionNameIdentifier == null)
            return;

        Optional<Holder.Reference<Potion>> holder = BuiltInRegistries.POTION.get(potionNameIdentifier);
        if (holder.isEmpty())
            return;

        potion = holder.get();
        potionType = optionalPotionType.get();
    }

    @Override
    protected void saveAdditional(@NotNull ValueOutput valueOutput)
    {
        if (potion == null)
            return;

        Identifier potionResource = BuiltInRegistries.POTION.getKey(potion.value());
        if (potionResource == null)
            return;

        String potionName = potionResource.toString();

        valueOutput.putString("PotionName", potionName);
        valueOutput.putString("PotionType", potionType);
    }

    @Override
    @Nullable
    public Packet<@NotNull ClientGamePacketListener> getUpdatePacket()
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
