package dev.maxoduke.mods.potioncauldron;

import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlock;
import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockEntity;
import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockInteraction;
import dev.maxoduke.mods.potioncauldron.config.ConfigManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SuppressWarnings({ "SpellCheckingInspection", "DataFlowIssue" })
public class PotionCauldron
{
    public static final String MOD_ID = "potioncauldron";
    public static final String MOD_NAME = "Potion Cauldron";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);

    public static final String BLOCK_NAME = "potion_cauldron";
    public static final String BLOCK_ENTITY_NAME = "potion_cauldron_block_entity";
    public static final String POTION_EVAPORATES_SOUND_NAME = "potion_evaporates";
    public static final ResourceLocation POTION_EVAPORATES_SOUND_ID = new ResourceLocation(MOD_ID, POTION_EVAPORATES_SOUND_NAME);

    public static final PotionCauldronBlock BLOCK;
    public static final BlockEntityType<PotionCauldronBlockEntity> BLOCK_ENTITY;
    public static final SoundEvent POTION_EVAPORATES_SOUND_EVENT;

    public static final ConfigManager CONFIG_MANAGER;

    static
    {
        BLOCK = new PotionCauldronBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON), PotionCauldronBlockInteraction.MAP);
        BLOCK_ENTITY = BlockEntityType.Builder.of(PotionCauldronBlockEntity::new, PotionCauldron.BLOCK).build(null);
        POTION_EVAPORATES_SOUND_EVENT = SoundEvent.createVariableRangeEvent(POTION_EVAPORATES_SOUND_ID);

        CONFIG_MANAGER = new ConfigManager();
    }
}
