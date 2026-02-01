package dev.maxoduke.mods.potioncauldron.networking.payloads;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class ParticlePayload implements CustomPacketPayload
{
    public static final Type<@NotNull ParticlePayload> TYPE = new Type<>(PotionCauldron.PARTICLES_CHANNEL);

    private final String particleType;
    private final BlockPos blockPos;
    private final int color;
    private final boolean generateMultiple;

    public ParticlePayload(ParticleType<?> particleType, BlockPos blockPos)
    {
        this(particleType, blockPos, 0, false);
    }

    @SuppressWarnings("DataFlowIssue")
    public ParticlePayload(ParticleType<?> particleType, BlockPos blockPos, int color, boolean generateMultiple)
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
    public Type<? extends @NotNull CustomPacketPayload> type()
    {
        return TYPE;
    }

    @SuppressWarnings("OptionalIsPresent")
    public Optional<ParticleType<?>> getParticleType()
    {
        Optional<Holder.Reference<ParticleType<?>>> holder = BuiltInRegistries.PARTICLE_TYPE.get(Identifier.parse(particleType));
        if (holder.isEmpty())
            return Optional.empty();

        return Optional.of(holder.get().value());
    }

    public BlockPos getBlockPos() { return blockPos; }
    public int getColor() { return color; }
    public boolean shouldGenerateMultiple() { return generateMultiple; }
}
