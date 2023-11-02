package dev.maxoduke.mods.potioncauldron.platform;

import dev.maxoduke.mods.potioncauldron.FabricInitializer;
import dev.maxoduke.mods.potioncauldron.config.ClientConfig;
import dev.maxoduke.mods.potioncauldron.networking.packets.ParticlePacket;
import dev.maxoduke.mods.potioncauldron.platform.services.INetworkHelper;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public class FabricNetworkHelper implements INetworkHelper
{
    public void sendConfigToClient(ServerPlayer player, ClientConfig config)
    {
        ServerPlayNetworking.send(player, FabricInitializer.CONFIG_CHANNEL, config.asBuf());
    }

    public void sendParticlesToClient(ServerPlayer player, ParticlePacket particlePacket)
    {
        ServerPlayNetworking.send(player, FabricInitializer.PARTICLES_CHANNEL, particlePacket.asBuf());
    }
}
