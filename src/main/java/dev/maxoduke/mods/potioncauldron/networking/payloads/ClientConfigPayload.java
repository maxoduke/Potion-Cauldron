package dev.maxoduke.mods.potioncauldron.networking.payloads;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.config.IConfig;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public class ClientConfigPayload implements CustomPacketPayload, IConfig
{
    public static final CustomPacketPayload.Type<ClientConfigPayload> TYPE = new Type<>(PotionCauldron.CONFIG_CHANNEL);

    private final boolean evaporatePotionWhenMixed;
    private final boolean allowMergingPotions;
    private final boolean allowCreatingTippedArrows;

    public ClientConfigPayload(boolean evaporatePotionWhenMixed, boolean allowMergingPotions, boolean allowCreatingTippedArrows)
    {
        this.evaporatePotionWhenMixed = evaporatePotionWhenMixed;
        this.allowMergingPotions = allowMergingPotions;
        this.allowCreatingTippedArrows = allowCreatingTippedArrows;
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public void writeToBuf(FriendlyByteBuf buf)
    {
        buf.writeBoolean(evaporatePotionWhenMixed);
        buf.writeBoolean(allowMergingPotions);
        buf.writeBoolean(allowCreatingTippedArrows);
    }

    public static ClientConfigPayload fromBuf(FriendlyByteBuf buf)
    {
        return new ClientConfigPayload(
            buf.readBoolean(),
            buf.readBoolean(),
            buf.readBoolean()
        );
    }

    public boolean shouldEvaporatePotionWhenMixed() { return evaporatePotionWhenMixed; }
    public boolean shouldAllowMergingPotions() { return allowMergingPotions; }
    public boolean shouldAllowCreatingTippedArrows() { return allowCreatingTippedArrows; }
}
