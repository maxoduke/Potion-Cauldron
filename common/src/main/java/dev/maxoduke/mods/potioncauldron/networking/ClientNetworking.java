package dev.maxoduke.mods.potioncauldron.networking;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.config.ClientConfig;
import dev.maxoduke.mods.potioncauldron.networking.packets.ParticlePacket;
import dev.maxoduke.mods.potioncauldron.util.ParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;

public class ClientNetworking
{
    public static void clientDisconnected()
    {
        PotionCauldron.CONFIG_MANAGER.setClientConfig(null);
    }

    public static void receiveConfigFromServer(ClientConfig config)
    {
        PotionCauldron.CONFIG_MANAGER.setClientConfig(config);
    }

    public static void receiveParticlesFromServer(ParticlePacket particleInfo)
    {
        Level level = Minecraft.getInstance().level;
        ParticleOptions particleType = particleInfo.getParticleType();

        if (particleType == ParticleTypes.EFFECT)
            ParticleUtils.generatePotionParticles(level, particleInfo.getBlockPos(), particleInfo.getColor(), particleInfo.shouldGenerateMultiple());
        else if (particleType == ParticleTypes.POOF)
            ParticleUtils.generateEvaporationParticles(level, particleInfo.getBlockPos());
    }
}
