package dev.maxoduke.mods.potioncauldron.platform.services;

import dev.maxoduke.mods.potioncauldron.networking.payloads.ClientConfigPayload;
import dev.maxoduke.mods.potioncauldron.networking.payloads.ParticlePayload;
import net.minecraft.server.level.ServerPlayer;

public interface INetworkHelper
{
    void sendConfigToClient(ServerPlayer player, ClientConfigPayload config);
    void sendParticlesToClient(ServerPlayer player, ParticlePayload particleInfo);
}
