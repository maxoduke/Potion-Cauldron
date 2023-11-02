package dev.maxoduke.mods.potioncauldron.networking;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.config.ClientConfig;
import dev.maxoduke.mods.potioncauldron.networking.packets.ParticlePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler
{
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE;

    static
    {
        INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(PotionCauldron.MOD_ID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
        );
    }

    public static void register()
    {
        INSTANCE.registerMessage(1, ClientConfig.class, ClientConfig::writeToBuf, ClientConfig::fromBuf, (config, contextSupplier) ->
        {
            contextSupplier.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientNetworking.receiveConfigFromServer(config))
            );
            contextSupplier.get().setPacketHandled(true);
        });

        INSTANCE.registerMessage(2, ParticlePacket.class, ParticlePacket::writeToBuf, ParticlePacket::fromBuf, (packet, contextSupplier) ->
        {
            contextSupplier.get().enqueueWork(() ->
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientNetworking.receiveParticlesFromServer(packet))
            );
            contextSupplier.get().setPacketHandled(true);
        });
    }
}
