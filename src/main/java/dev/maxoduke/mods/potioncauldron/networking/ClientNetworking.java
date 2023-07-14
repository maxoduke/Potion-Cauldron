package dev.maxoduke.mods.potioncauldron.networking;

import com.google.gson.Gson;
import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlock;
import dev.maxoduke.mods.potioncauldron.config.ClientConfig;
import dev.maxoduke.mods.potioncauldron.networking.packets.ParticlePacket;
import dev.maxoduke.mods.potioncauldron.util.ParticleUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.client.player.ClientPickBlockGatherCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;

@Environment(EnvType.CLIENT)
public class ClientNetworking
{
    public static void registerEvents()
    {
        ClientPlayNetworking.registerGlobalReceiver(PotionCauldron.CONFIG_CHANNEL, ClientNetworking::receiveConfigFromServer);
        ClientPlayNetworking.registerGlobalReceiver(PotionCauldron.PARTICLES_CHANNEL, ClientNetworking::receiveParticlesFromServer);

        ClientPlayConnectionEvents.DISCONNECT.register(PotionCauldron.CONFIG_CHANNEL, ClientNetworking::clientDisconnected);
        ClientPickBlockGatherCallback.EVENT.register(PotionCauldronBlock::pickedWithPickBlock);
    }

    public static void receiveParticlesFromServer(Minecraft client, ClientPacketListener ignoredHandler, FriendlyByteBuf buf, PacketSender ignoredResponseSender)
    {
        ParticlePacket particleInfo = ParticlePacket.fromPacket(buf);
        ParticleOptions particleType = particleInfo.getParticleType();

        if (particleType == ParticleTypes.EFFECT)
            ParticleUtils.generatePotionParticles(client.level, particleInfo.getBlockPos(), particleInfo.getColor(), particleInfo.shouldGenerateMultiple());
        else if (particleType == ParticleTypes.POOF)
            ParticleUtils.generateEvaporationParticles(client.level, particleInfo.getBlockPos());
    }

    private static void receiveConfigFromServer(Minecraft ignoredClient, ClientPacketListener ignoredHandler, FriendlyByteBuf buf, PacketSender ignoredResponseSender)
    {
        ClientConfig clientConfig = new Gson().fromJson(new String(buf.readByteArray()), ClientConfig.class);
        PotionCauldron.CONFIG_MANAGER.setClientConfig(clientConfig);
    }

    private static void clientDisconnected(ClientPacketListener ignoredClientPacketListener, Minecraft ignoredMinecraft)
    {
        PotionCauldron.CONFIG_MANAGER.setClientConfig(null);
    }
}
