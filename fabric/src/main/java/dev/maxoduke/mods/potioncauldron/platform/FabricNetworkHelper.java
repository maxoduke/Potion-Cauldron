package dev.maxoduke.mods.potioncauldron.platform;

import dev.maxoduke.mods.potioncauldron.networking.payloads.ClientConfigPayload;
import dev.maxoduke.mods.potioncauldron.networking.payloads.ParticlePayload;
import dev.maxoduke.mods.potioncauldron.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetworkHelper implements INetworkHelper
{
    public void sendConfigToClient(ServerPlayer player, ClientConfigPayload config)
    {
        ServerPlayNetworking.send(player, config);
    }

    public void sendParticlesToClient(ServerPlayer player, ParticlePayload particleInfo)
    {
        ServerPlayNetworking.send(player, particleInfo);
    }
}
