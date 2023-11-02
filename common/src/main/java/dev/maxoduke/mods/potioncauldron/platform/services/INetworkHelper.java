package dev.maxoduke.mods.potioncauldron.platform.services;

import dev.maxoduke.mods.potioncauldron.config.ClientConfig;
import dev.maxoduke.mods.potioncauldron.networking.packets.ParticlePacket;
import net.minecraft.server.level.ServerPlayer;

public interface INetworkHelper
{
    void sendConfigToClient(ServerPlayer player, ClientConfig config);
    void sendParticlesToClient(ServerPlayer player, ParticlePacket particlePacket);
}
