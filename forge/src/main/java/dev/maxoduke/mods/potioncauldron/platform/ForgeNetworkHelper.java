package dev.maxoduke.mods.potioncauldron.platform;

import dev.maxoduke.mods.potioncauldron.networking.NetworkHandler;
import dev.maxoduke.mods.potioncauldron.networking.payloads.ClientConfigPayload;
import dev.maxoduke.mods.potioncauldron.networking.payloads.ParticlePayload;
import dev.maxoduke.mods.potioncauldron.platform.services.INetworkHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public class ForgeNetworkHelper implements INetworkHelper
{
    public void sendConfigToClient(ServerPlayer player, ClientConfigPayload config)
    {
        NetworkHandler.INSTANCE.send(config, PacketDistributor.PLAYER.with(player));
    }

    public void sendParticlesToClient(ServerPlayer player, ParticlePayload particleInfo)
    {
        NetworkHandler.INSTANCE.send(particleInfo, PacketDistributor.PLAYER.with(player));
    }
}
