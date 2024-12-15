package dev.maxoduke.mods.potioncauldron.networking;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.networking.payloads.ClientConfigPayload;
import dev.maxoduke.mods.potioncauldron.networking.payloads.ParticlePayload;
import dev.maxoduke.mods.potioncauldron.util.ParticleUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class ClientNetworking
{
    public static void clientDisconnected()
    {
        PotionCauldron.CONFIG_MANAGER.setClientConfig(null);
    }

    public static void receiveConfigFromServer(ClientConfigPayload config)
    {
        PotionCauldron.CONFIG_MANAGER.setClientConfig(config);
    }

    public static void receiveParticlesFromServer(ParticlePayload particleInfo)
    {
        Level level = Minecraft.getInstance().level;
        Optional<SimpleParticleType> particleTypeHolder = particleInfo.getParticleType();
        if (particleTypeHolder.isEmpty())
            return;

        SimpleParticleType particleType = particleTypeHolder.get();
        if (particleType == ParticleTypes.EFFECT)
            ParticleUtils.generatePotionParticles(level, particleInfo.getBlockPos(), particleInfo.getColor(), particleInfo.shouldGenerateMultiple());
        else if (particleType == ParticleTypes.POOF)
            ParticleUtils.generateEvaporationParticles(level, particleInfo.getBlockPos());
    }
}
