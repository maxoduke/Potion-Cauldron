package dev.maxoduke.mods.potioncauldron;

import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlock;
import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockEntity;
import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockEntityRenderer;
import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockInteraction;
import dev.maxoduke.mods.potioncauldron.commands.CommandHandlers;
import dev.maxoduke.mods.potioncauldron.config.ConfigManager;
import dev.maxoduke.mods.potioncauldron.networking.ClientNetworking;
import dev.maxoduke.mods.potioncauldron.networking.ServerNetworking;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PotionCauldron
{
    public static final ResourceLocation CONFIG_CHANNEL = new ResourceLocation(Mod.ID, "config_channel");
    public static final ResourceLocation PARTICLES_CHANNEL = new ResourceLocation(Mod.ID, "particles_channel");

    public static final ResourceLocation POTION_EVAPORATES_SOUND_ID = new ResourceLocation(Mod.ID, "potion_evaporates");
    public static final SoundEvent POTION_EVAPORATES_SOUND_EVENT = SoundEvent.createVariableRangeEvent(POTION_EVAPORATES_SOUND_ID);

    public static final PotionCauldronBlock BLOCK;
    public static final BlockEntityType<PotionCauldronBlockEntity> BLOCK_ENTITY;
    public static final ConfigManager CONFIG_MANAGER;

    static
    {
        Registry.register(BuiltInRegistries.SOUND_EVENT, POTION_EVAPORATES_SOUND_ID, POTION_EVAPORATES_SOUND_EVENT);

        BLOCK = new PotionCauldronBlock(
            BlockBehaviour.Properties.copy(Blocks.CAULDRON),
            PotionCauldronBlockInteraction.MAP
        );

        Registry.register(
            BuiltInRegistries.BLOCK,
            new ResourceLocation(Mod.ID, PotionCauldronBlock.NAME),
            BLOCK
        );

        BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation(Mod.ID, "potion_cauldron_block_entity"),
            FabricBlockEntityTypeBuilder.create(PotionCauldronBlockEntity::new, BLOCK).build()
        );

        CONFIG_MANAGER = new ConfigManager();
    }

    public static void initialize()
    {
        PotionCauldronBlockInteraction.bootstrap();
        ServerNetworking.registerEvents();

        CommandRegistrationCallback.EVENT.register(CommandHandlers::register);
    }

    @Environment(EnvType.CLIENT)
    public static void initializeClient()
    {
        BlockEntityRenderers.register(BLOCK_ENTITY, PotionCauldronBlockEntityRenderer::new);
        ClientNetworking.registerEvents();
    }
}
