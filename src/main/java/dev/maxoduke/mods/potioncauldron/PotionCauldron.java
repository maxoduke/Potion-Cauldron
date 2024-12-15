package dev.maxoduke.mods.potioncauldron;

import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlock;
import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockEntity;
import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockInteraction;
import dev.maxoduke.mods.potioncauldron.config.ConfigManager;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({ "SpellCheckingInspection" })
public class PotionCauldron
{
    public static final String MOD_ID = "potioncauldron";
    public static final String MOD_NAME = "Potion Cauldron";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);

    public static final String BLOCK_NAME = "potion_cauldron";
    public static final String BLOCK_ENTITY_NAME = "potion_cauldron_block_entity";
    public static final String POTION_EVAPORATES_SOUND_NAME = "potion_evaporates";
    public static final ResourceLocation POTION_EVAPORATES_SOUND_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, POTION_EVAPORATES_SOUND_NAME);

    public static final ResourceLocation CONFIG_CHANNEL = ResourceLocation.fromNamespaceAndPath(PotionCauldron.MOD_ID, "config_channel");
    public static final ResourceLocation PARTICLES_CHANNEL = ResourceLocation.fromNamespaceAndPath(PotionCauldron.MOD_ID, "particles_channel");

    public static final PotionCauldronBlock BLOCK = (PotionCauldronBlock) Blocks.register(
        ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(MOD_ID, BLOCK_NAME)),
        properties -> new PotionCauldronBlock(PotionCauldronBlockInteraction.INTERACTION_MAP, properties),
        BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)
    );

    public static final BlockEntityType<PotionCauldronBlockEntity> BLOCK_ENTITY = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        ResourceLocation.fromNamespaceAndPath(MOD_ID, BLOCK_ENTITY_NAME),
        FabricBlockEntityTypeBuilder.create(
            PotionCauldronBlockEntity::new,
            PotionCauldron.BLOCK
        ).build()
    );

    public static final SoundEvent POTION_EVAPORATES_SOUND_EVENT = SoundEvent.createVariableRangeEvent(POTION_EVAPORATES_SOUND_ID);
    public static final ConfigManager CONFIG_MANAGER = new ConfigManager();

    static
    {
        PotionCauldronBlockInteraction.bootstrap();
    }
}
