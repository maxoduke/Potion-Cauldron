package dev.maxoduke.mods.potioncauldron.networking;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.config.ServerConfig;
import dev.maxoduke.mods.potioncauldron.networking.payloads.ClientConfigPayload;
import dev.maxoduke.mods.potioncauldron.networking.payloads.ParticlePayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
        ClientConfigPayload config = PotionCauldron.CONFIG_MANAGER.serverConfig().asClientConfig();
        ServerPlayNetworking.send(player, config);
    }

    public static void sendConfigToAllClients()
    {
        if (server == null)
            return;

        ClientConfigPayload config = PotionCauldron.CONFIG_MANAGER.serverConfig().asClientConfig();
        for (ServerPlayer player : server.getPlayerList().getPlayers())
            ServerPlayNetworking.send(player, config);
    }

    public static void sendParticlesToClients(ParticlePayload particleInfo)
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
                ServerPlayNetworking.send(player, particleInfo);
        }
    }
}
