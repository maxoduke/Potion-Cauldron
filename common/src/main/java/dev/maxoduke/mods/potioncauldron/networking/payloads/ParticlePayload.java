package dev.maxoduke.mods.potioncauldron.networking.payloads;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;

public class ParticlePayload implements CustomPacketPayload
{
    public static final StreamCodec<FriendlyByteBuf, ParticlePayload> CODEC = CustomPacketPayload.codec(ParticlePayload::writeToBuf, ParticlePayload::new);
    public static final Type<ParticlePayload> TYPE = new Type<>(PotionCauldron.PARTICLES_CHANNEL);

    private final String particleType;
    private final BlockPos blockPos;
    private final int color;
    private final boolean generateMultiple;

    public ParticlePayload(SimpleParticleType particleType, BlockPos blockPos)
    {
        this(particleType, blockPos, 0, false);
    }

    @SuppressWarnings("DataFlowIssue")
    public ParticlePayload(SimpleParticleType particleType, BlockPos blockPos, int color, boolean generateMultiple)
    {
        this.particleType = BuiltInRegistries.PARTICLE_TYPE.getKey(particleType).toString();
        this.blockPos = blockPos;
        this.color = color;
        this.generateMultiple = generateMultiple;
    }

    public ParticlePayload(String particleType, BlockPos blockPos, int color, boolean generateMultiple)
    {
        this.particleType = particleType;
        this.blockPos = blockPos;
        this.color = color;
        this.generateMultiple = generateMultiple;
    }

    public ParticlePayload(FriendlyByteBuf buf)
    {
        this(
            buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString(),
            buf.readNullable(BlockPos.STREAM_CODEC),
            buf.readInt(),
            buf.readBoolean()
        );
    }

    public void writeToBuf(FriendlyByteBuf buf)
    {
        buf.writeInt(particleType.length());
        buf.writeCharSequence(particleType, StandardCharsets.UTF_8);
        buf.writeNullable(blockPos, BlockPos.STREAM_CODEC);
        buf.writeInt(color);
        buf.writeBoolean(generateMultiple);
    }

    public static ParticlePayload fromBuf(FriendlyByteBuf buf)
    {
        return new ParticlePayload(
            buf.readCharSequence(buf.readInt(), StandardCharsets.UTF_8).toString(),
            buf.readNullable(BlockPos.STREAM_CODEC),
            buf.readInt(),
            buf.readBoolean()
        );
    }

    @Override
    @NotNull
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public SimpleParticleType getParticleType() { return (SimpleParticleType) BuiltInRegistries.PARTICLE_TYPE.get(new ResourceLocation(particleType)); }
    public BlockPos getBlockPos() { return blockPos; }
    public int getColor() { return color; }
    public boolean shouldGenerateMultiple() { return generateMultiple; }
}
