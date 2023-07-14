package dev.maxoduke.mods.potioncauldron.networking;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.config.ServerConfig;
import dev.maxoduke.mods.potioncauldron.networking.packets.ParticlePacket;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class ServerNetworking
{
    private static MinecraftServer server;

    public static void registerEvents()
    {
        ServerLifecycleEvents.SERVER_STARTING.register(ServerNetworking::serverStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(ServerNetworking::serverStopping);

        ServerPlayConnectionEvents.JOIN.register(ServerNetworking::sendConfigToClient);
    }

    private static void serverStarting(MinecraftServer minecraftServer)
    {
        server = minecraftServer;
        PotionCauldron.CONFIG_MANAGER.setServerConfig(ServerConfig.load());
    }

    private static void serverStopping(MinecraftServer ignored)
    {
        PotionCauldron.CONFIG_MANAGER.setServerConfig(null);
        server = null;
    }

    private static void sendConfigToClient(@NotNull ServerGamePacketListenerImpl handler, PacketSender ignoredPacketSender, MinecraftServer ignoredServer)
    {
        ServerPlayNetworking.send(handler.getPlayer(), PotionCauldron.CONFIG_CHANNEL, PotionCauldron.CONFIG_MANAGER.serverConfig().asClientPacket());
    }

    public static void sendConfigToAllClients()
    {
        if (server == null)
            return;

        for (ServerPlayer player : PlayerLookup.all(server))
            ServerPlayNetworking.send(player, PotionCauldron.CONFIG_CHANNEL, PotionCauldron.CONFIG_MANAGER.serverConfig().asClientPacket());
    }

    public static void sendParticlesToClients(ParticlePacket particleInfo)
    {
        FriendlyByteBuf packet = particleInfo.asPacket();

        BlockPos particlePosition = particleInfo.getBlockPos();
        Vec3 position = new Vec3(
            particlePosition.getX(),
            particlePosition.getY(),
            particlePosition.getZ()
        );

        for (ServerPlayer player : PlayerLookup.all(server))
        {
            BlockPos playerPosition = player.blockPosition();
            if (playerPosition.closerToCenterThan(position, 32.0))
                ServerPlayNetworking.send(player, PotionCauldron.PARTICLES_CHANNEL, packet);
        }
    }
}
