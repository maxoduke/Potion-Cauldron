package dev.maxoduke.mods.potioncauldron.networking;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.config.ClientConfig;
import dev.maxoduke.mods.potioncauldron.networking.packets.ParticlePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler
{
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE;

    static
    {
        INSTANCE = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(PotionCauldron.MOD_ID, "main"))
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .simpleChannel();
    }

    public static void register()
    {
        INSTANCE
            .messageBuilder(ClientConfig.class, 1, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(ClientConfig::writeToBuf)
            .decoder(ClientConfig::fromBuf)
            .consumerMainThread((config, context) -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientNetworking.receiveConfigFromServer(config)))
            .add();

        INSTANCE
            .messageBuilder(ParticlePacket.class, 2, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(ParticlePacket::writeToBuf)
            .decoder(ParticlePacket::fromBuf)
            .consumerMainThread((packet, context) -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientNetworking.receiveParticlesFromServer(packet)))
            .add();
    }
}
