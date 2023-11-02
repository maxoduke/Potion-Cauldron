package dev.maxoduke.mods.potioncauldron.platform;

import dev.maxoduke.mods.potioncauldron.config.ClientConfig;
import dev.maxoduke.mods.potioncauldron.networking.NetworkHandler;
import dev.maxoduke.mods.potioncauldron.networking.packets.ParticlePacket;
import dev.maxoduke.mods.potioncauldron.platform.services.INetworkHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;

public class ForgeNetworkHelper implements INetworkHelper
{
    public void sendConfigToClient(ServerPlayer player, ClientConfig config)
    {
        NetworkHandler.INSTANCE.sendTo(config, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public void sendParticlesToClient(ServerPlayer player, ParticlePacket particlePacket)
    {
        NetworkHandler.INSTANCE.sendTo(particlePacket, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
}
