package dev.maxoduke.mods.potioncauldron.platform;

import dev.maxoduke.mods.potioncauldron.config.ClientConfig;
import dev.maxoduke.mods.potioncauldron.networking.NetworkHandler;
import dev.maxoduke.mods.potioncauldron.networking.packets.ParticlePacket;
import dev.maxoduke.mods.potioncauldron.platform.services.INetworkHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class ForgeNetworkHelper implements INetworkHelper
{
    public void sendConfigToClient(ServerPlayer player, ClientConfig config)
    {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), config);
    }

    public void sendParticlesToClient(ServerPlayer player, ParticlePacket particlePacket)
    {
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), particlePacket);
    }
}
