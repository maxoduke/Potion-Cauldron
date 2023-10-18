package dev.maxoduke.mods.potioncauldron.networking;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.config.ClientConfig;
import dev.maxoduke.mods.potioncauldron.config.ServerConfig;
import dev.maxoduke.mods.potioncauldron.networking.packets.ParticlePacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;

public class ServerNetworking
{
    private static MinecraftServer server;

    public static void registerEvents()
    {
        ServerLifecycleEvents.SERVER_STARTING.register(ServerNetworking::serverStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerNetworking::serverStopping);

        ServerPlayConnectionEvents.JOIN.register(ServerNetworking::sendConfigToClient);
    }

    public static void serverStarting(MinecraftServer minecraftServer)
    {
        server = minecraftServer;
        PotionCauldron.CONFIG_MANAGER.setServerConfig(ServerConfig.load());
    }

    public static void serverStopping(MinecraftServer ignored)
    {
        PotionCauldron.CONFIG_MANAGER.setServerConfig(null);
        server = null;
    }

    public static void sendConfigToClient(ServerGamePacketListenerImpl listener, PacketSender sender, MinecraftServer server)
    {
        ClientConfig config = PotionCauldron.CONFIG_MANAGER.serverConfig().asClientConfig();
        ServerPlayNetworking.send(listener.player, PotionCauldron.CONFIG_CHANNEL, config.asBuf());
    }

    public static void sendConfigToAllClients()
    {
        if (server == null)
            return;

        ClientConfig config = PotionCauldron.CONFIG_MANAGER.serverConfig().asClientConfig();
        for (ServerPlayer player : server.getPlayerList().getPlayers())
            ServerPlayNetworking.send(player, PotionCauldron.CONFIG_CHANNEL, config.asBuf());
    }

    public static void sendParticlesToClients(ParticlePacket particleInfo)
    {
        BlockPos particlePosition = particleInfo.getBlockPos();
        Vec3 position = new Vec3(
            particlePosition.getX(),
            particlePosition.getY(),
            particlePosition.getZ()
        );

        for (ServerPlayer player : server.getPlayerList().getPlayers())
        {
            BlockPos playerPosition = player.blockPosition();
            if (playerPosition.closerToCenterThan(position, 32.0))
                ServerPlayNetworking.send(player, PotionCauldron.PARTICLES_CHANNEL, particleInfo.asBuf());
        }
    }
}
