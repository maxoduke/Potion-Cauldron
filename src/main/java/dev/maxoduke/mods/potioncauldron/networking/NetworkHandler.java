package dev.maxoduke.mods.potioncauldron.networking;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.networking.payloads.ClientConfigPayload;
import dev.maxoduke.mods.potioncauldron.networking.payloads.ParticlePayload;
import net.minecraft.resources.Identifier;
import net.minecraftforge.network.*;

@SuppressWarnings({ "deprecation" })
public class NetworkHandler
{
    private static final int PROTOCOL_VERSION = 1;
    public static final SimpleChannel INSTANCE;

    static
    {
        INSTANCE = ChannelBuilder
            .named(Identifier.fromNamespaceAndPath(PotionCauldron.MOD_ID, "main"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .clientAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
            .serverAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
            .simpleChannel();
    }

    public static void register()
    {
        INSTANCE
            .messageBuilder(ClientConfigPayload.class, 1, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(ClientConfigPayload::writeToBuf)
            .decoder(ClientConfigPayload::fromBuf)
            .consumerMainThread((config, ignoredContext) -> ClientNetworking.receiveConfigFromServer(config))
            .add();

        INSTANCE
            .messageBuilder(ParticlePayload.class, 2, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(ParticlePayload::writeToBuf)
            .decoder(ParticlePayload::fromBuf)
            .consumerMainThread((packet, ignoredContext) -> ClientNetworking.receiveParticlesFromServer(packet))
            .add();
    }
}
