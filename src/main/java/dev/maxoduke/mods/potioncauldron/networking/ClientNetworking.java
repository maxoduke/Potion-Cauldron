package dev.maxoduke.mods.potioncauldron.networking;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.config.ClientConfig;
import dev.maxoduke.mods.potioncauldron.networking.packets.ParticlePacket;
import dev.maxoduke.mods.potioncauldron.util.ParticleUtils;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

public class ClientNetworking
{
    public static void registerEvents()
    {
        ClientPlayNetworking.registerGlobalReceiver(PotionCauldron.CONFIG_CHANNEL, ClientNetworking::receiveConfigFromServer);
        ClientPlayNetworking.registerGlobalReceiver(PotionCauldron.PARTICLES_CHANNEL, ClientNetworking::receiveParticlesFromServer);

        ClientPlayConnectionEvents.DISCONNECT.register(PotionCauldron.CONFIG_CHANNEL, ClientNetworking::clientDisconnected);
    }

    public static void clientDisconnected(ClientPacketListener listener, Minecraft client)
    {
        PotionCauldron.CONFIG_MANAGER.setClientConfig(null);
    }

    public static void receiveConfigFromServer(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender)
    {
        PotionCauldron.CONFIG_MANAGER.setClientConfig(ClientConfig.fromBuf(buf));
    }

    public static void receiveParticlesFromServer(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender)
    {
        ParticlePacket particleInfo = ParticlePacket.fromBuf(buf);

        Level level = Minecraft.getInstance().level;
        ParticleOptions particleType = particleInfo.getParticleType();

        if (particleType == ParticleTypes.EFFECT)
            ParticleUtils.generatePotionParticles(level, particleInfo.getBlockPos(), particleInfo.getColor(), particleInfo.shouldGenerateMultiple());
        else if (particleType == ParticleTypes.POOF)
            ParticleUtils.generateEvaporationParticles(level, particleInfo.getBlockPos());
    }
}
