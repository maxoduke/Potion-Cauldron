package dev.maxoduke.mods.potioncauldron.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.particle.Particle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Environment(EnvType.CLIENT)
@SuppressWarnings("DataFlowIssue")
public class ParticleUtils
{
    public static void generatePotionParticles(Level level, BlockPos pos, int color, boolean generateMultiple)
    {
        ParticleStatus particleStatus = Minecraft.getInstance().options.particles().get();
        if (particleStatus == ParticleStatus.MINIMAL)
            return;

        BlockState stateOfBlockAbove = level.getBlockState(pos.above());
        if (stateOfBlockAbove.canOcclude())
            return;

        RandomSource random = RandomSource.create();
        int multiplier, numberOfParticles = 1;

        if (generateMultiple)
        {
            multiplier = particleStatus == ParticleStatus.DECREASED ? 1 : 2;
            numberOfParticles = random.nextInt(3 * multiplier, 5 * multiplier);
        }
        else
        {
            if (particleStatus == ParticleStatus.DECREASED && random.nextInt(10) % 5 != 0)
                return;
            else if (random.nextInt(10) % 3 != 0)
                return;
        }

        float red = (color >> 16 & 255) / 255.0f;
        float green = (color >> 8 & 255) / 255.0f;
        float blue = (color & 255) / 255.0f;

        for (int i = 1; i <= numberOfParticles; i++)
        {
            Particle particle = Minecraft.getInstance().particleEngine.createParticle(
                ParticleTypes.EFFECT,
                pos.getX() + 0.45 + random.nextDouble() * 0.2,
                pos.getY() + 1.0,
                pos.getZ() + 0.45 + random.nextDouble() * 0.2,
                0.7,
                1.3,
                0.7
            );

            particle.setColor(red, green, blue);
        }
    }

    public static void generateEvaporationParticles(Level level, BlockPos pos)
    {
        ParticleStatus particleStatus = Minecraft.getInstance().options.particles().get();
        if (particleStatus == ParticleStatus.MINIMAL)
            return;

        BlockState stateOfBlockAbove = level.getBlockState(pos.above());
        if (stateOfBlockAbove.canOcclude())
            return;

        int maxParticles = particleStatus == ParticleStatus.DECREASED ? 5 : 10;

        for (int i = 1; i <= maxParticles; i++)
        {
            Minecraft.getInstance().particleEngine.createParticle(
                ParticleTypes.POOF,
                pos.getX() + 0.45,
                pos.getY() + 1.0,
                pos.getZ() + 0.45,
                0,
                0.02,
                0
            );
        }
    }
}
