package dev.maxoduke.mods.potioncauldron.networking.packets;

import com.google.gson.Gson;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.nio.charset.StandardCharsets;

public class ParticlePacket
{
    private final String particleType;
    private final BlockPos blockPos;
    private final int color;
    private final boolean generateMultiple;

    public ParticlePacket(ParticleOptions particleType, BlockPos blockPos)
    {
        this(particleType, blockPos, 0, false);
    }

    public ParticlePacket(ParticleOptions particleType, BlockPos blockPos, int color, boolean generateMultiple)
    {
        this.particleType = particleType.writeToString();
        this.blockPos = blockPos;
        this.color = color;
        this.generateMultiple = generateMultiple;
    }

    public ParticleOptions getParticleType() { return (ParticleOptions) BuiltInRegistries.PARTICLE_TYPE.get(new ResourceLocation(particleType)); }

    public BlockPos getBlockPos() { return blockPos; }

    public int getColor() { return color; }

    public boolean shouldGenerateMultiple() { return generateMultiple; }

    public FriendlyByteBuf asPacket()
    {
        FriendlyByteBuf buf = PacketByteBufs.create();

        String json = new Gson().toJson(this, ParticlePacket.class);
        buf.writeByteArray(json.getBytes(StandardCharsets.UTF_8));

        return buf;
    }

    public static ParticlePacket fromPacket(FriendlyByteBuf buf)
    {
        String json = new String(buf.readByteArray());
        return new Gson().fromJson(json, ParticlePacket.class);
    }
}
