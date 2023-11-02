package dev.maxoduke.mods.potioncauldron.networking;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.config.ClientConfig;
import dev.maxoduke.mods.potioncauldron.config.ServerConfig;
import dev.maxoduke.mods.potioncauldron.networking.packets.ParticlePacket;
import dev.maxoduke.mods.potioncauldron.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public class ServerNetworking
{
    private static MinecraftServer server;

    public static void serverStarting(MinecraftServer minecraftServer)
    {
        server = minecraftServer;
        PotionCauldron.CONFIG_MANAGER.setServerConfig(ServerConfig.load());
    }

    public static void serverStopping()
    {
        PotionCauldron.CONFIG_MANAGER.setServerConfig(null);
        server = null;
    }

    public static void sendConfigToClient(ServerPlayer player)
    {
        ClientConfig config = PotionCauldron.CONFIG_MANAGER.serverConfig().asClientConfig();
        Services.NETWORK.sendConfigToClient(player, config);
    }

    public static void sendConfigToAllClients()
    {
        if (server == null)
            return;

        ClientConfig config = PotionCauldron.CONFIG_MANAGER.serverConfig().asClientConfig();
        for (ServerPlayer player : server.getPlayerList().getPlayers())
            Services.NETWORK.sendConfigToClient(player, config);
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
                Services.NETWORK.sendParticlesToClient(player, particleInfo);
        }
    }
}
