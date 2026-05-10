package dev.maxoduke.mods.potioncauldron;

import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlock;
import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockEntity;
import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockInteractions;
import dev.maxoduke.mods.potioncauldron.config.ConfigManager;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SpellCheckingInspection")
public class PotionCauldron
{
    public static final String MOD_ID = "potioncauldron";
    public static final String MOD_NAME = "Potion Cauldron";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);

    public static final String BLOCK_NAME = "potion_cauldron";
    public static final String BLOCK_ENTITY_NAME = "potion_cauldron_block_entity";
    public static final String POTION_EVAPORATES_SOUND_NAME = "potion_evaporates";
    public static final Identifier POTION_EVAPORATES_SOUND_ID = Identifier.fromNamespaceAndPath(MOD_ID, POTION_EVAPORATES_SOUND_NAME);

    public static final Identifier CONFIG_CHANNEL = Identifier.fromNamespaceAndPath(PotionCauldron.MOD_ID, "config_channel");
    public static final Identifier PARTICLES_CHANNEL = Identifier.fromNamespaceAndPath(PotionCauldron.MOD_ID, "particles_channel");

    public static final PotionCauldronBlock BLOCK = (PotionCauldronBlock) Blocks.register(
        ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, BLOCK_NAME)),
        properties -> new PotionCauldronBlock(Biome.Precipitation.RAIN, PotionCauldronBlockInteractions.POTION, properties),
        BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON)
    );

    public static final BlockEntityType<@NotNull PotionCauldronBlockEntity> BLOCK_ENTITY = Registry.register(
        BuiltInRegistries.BLOCK_ENTITY_TYPE,
        Identifier.fromNamespaceAndPath(MOD_ID, BLOCK_ENTITY_NAME),
        FabricBlockEntityTypeBuilder.create(
            PotionCauldronBlockEntity::new,
            PotionCauldron.BLOCK
        ).build()
    );

    public static final SoundEvent POTION_EVAPORATES_SOUND_EVENT = SoundEvent.createVariableRangeEvent(POTION_EVAPORATES_SOUND_ID);
    public static final ConfigManager CONFIG_MANAGER = new ConfigManager();

    static
    {
        PotionCauldronBlockInteractions.bootstrap();
    }
}
