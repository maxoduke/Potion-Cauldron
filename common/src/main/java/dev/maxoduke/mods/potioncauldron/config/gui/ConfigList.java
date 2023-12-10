package dev.maxoduke.mods.potioncauldron.config.gui;

import dev.maxoduke.mods.potioncauldron.config.ServerConfig;
import dev.maxoduke.mods.potioncauldron.config.gui.entries.*;
import dev.maxoduke.mods.potioncauldron.config.exceptions.InvalidConfigValueException;
import dev.maxoduke.mods.potioncauldron.util.ProbabilityUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

import java.math.BigDecimal;

@SuppressWarnings("DuplicatedCode")
public class ConfigList extends ContainerObjectSelectionList<ConfigList.Entry>
{
    private static final Component EVAPORATE_POTION_WHEN_MIXED = Component.translatable("config.text.evaporatePotionWhenMixed");
    private static final Component ALLOW_MERGING_POTIONS = Component.translatable("config.text.allowMergingPotions");

    private static final Component APPLY_POTION_EFFECTS = Component.translatable("config.text.applyPotionEffectsToEntitiesInside");
    private static final Component ALLOW_FILLING_WITH_WATER_DRIPS = Component.translatable("config.text.allowFillingWithWaterDrips");

    private static final Component ALLOW_CREATING_TIPPED_ARROWS = Component.translatable("config.text.allowCreatingTippedArrows");
    private static final Component MAX_TIPPED_ARROWS_PER_LEVEL = Component.translatable("config.text.maxTippedArrowsPerLevel");

    private static final Component GENERATE_IN_SWAMP_HUTS = Component.translatable("config.text.generateInSwampHuts");

    private static final Component POTION_TYPES_PERCENTAGE = Component.translatable("config.text.potionTypesPercentage");
    private static final Component NORMAL_POTION = Component.translatable("config.text.normalPotion");
    private static final Component SPLASH_POTION = Component.translatable("config.text.splashPotion");
    private static final Component LINGERING_POTION = Component.translatable("config.text.lingeringPotion");

    private static final Component POTION_LEVELS_PERCENTAGE = Component.translatable("config.text.potionLevelsPercentage");
    private static final Component LEVEL_1 = Component.translatable("config.text.level_1");
    private static final Component LEVEL_2 = Component.translatable("config.text.level_2");
    private static final Component LEVEL_3 = Component.translatable("config.text.level_3");

    private static final Component POTIONS_PERCENTAGE = Component.translatable("config.text.potionsPercentage");

    private final Font font;
    private final ServerConfig config;
    private final Runnable onChange;

    private final LabelEntry maxTippedArrowsPerLevelLabelEntry;
    private final EditboxEntry level1MaxTippedArrowsEntry;
    private final EditboxEntry level2MaxTippedArrowsEntry;
    private final EditboxEntry level3MaxTippedArrowsEntry;

    private final LabelEntry potionTypesLabelEntry;
    private final EditboxEntry normalPotionEntry;
    private final EditboxEntry splashPotionEntry;
    private final EditboxEntry lingeringPotionEntry;

    private final LabelEntry potionLevelsLabelEntry;
    private final EditboxEntry level1Entry;
    private final EditboxEntry level2Entry;
    private final EditboxEntry level3Entry;

    private final AddPotionEntry addPotionEntry;

    public ConfigList(ConfigScreen screen, Font font, ServerConfig config, Runnable onChange)
    {
        // super(Minecraft.getInstance(), screen.width, screen.height, 50, screen.height - 50);
        super(Minecraft.getInstance(), screen.width, screen.height - 100, 50, 25);

        this.config = config;
        this.font = font;
        this.onChange = onChange;

        boolean evaporatePotionWhenMixed = config.shouldEvaporatePotionWhenMixed();
        boolean allowMergingPotions = config.shouldAllowMergingPotions();
        boolean applyPotionEffects = config.shouldApplyPotionEffectsToEntitiesInside();
        boolean allowFillingWithWaterDrips = config.shouldAllowFillingWithWaterDrips();
        boolean allowCreatingTippedArrows = config.shouldAllowCreatingTippedArrows();
        boolean generateInSwampHuts = config.shouldGenerateInSwampHuts();

        addEntry(new OnOffButtonEntry(font, EVAPORATE_POTION_WHEN_MIXED, evaporatePotionWhenMixed, this::toggleEvaporatePotionWhenMixed));
        addEntry(new OnOffButtonEntry(font, ALLOW_MERGING_POTIONS, allowMergingPotions, this::toggleAllowMergingPotions));

        addEntry(new OnOffButtonEntry(font, APPLY_POTION_EFFECTS, applyPotionEffects, this::toggleApplyPotionEffects));
        addEntry(new OnOffButtonEntry(font, ALLOW_FILLING_WITH_WATER_DRIPS, allowFillingWithWaterDrips, this::toggleAllowFillingWithWaterDrips));

        addEntry(new BlankEntry(font));
        addEntry(new OnOffButtonEntry(font, ALLOW_CREATING_TIPPED_ARROWS, allowCreatingTippedArrows, this::toggleAllowCreatingTippedArrows));

        addEntry((maxTippedArrowsPerLevelLabelEntry = new LabelEntry(font, MAX_TIPPED_ARROWS_PER_LEVEL, 20)));
        addEntry((level1MaxTippedArrowsEntry = new EditboxEntry(font, LEVEL_1, 40, allowCreatingTippedArrows, String.valueOf(config.getMaxTippedArrowsForLevel1()), this::validateAndUpdateMaxTippedArrowsPerLevel)));
        addEntry((level2MaxTippedArrowsEntry = new EditboxEntry(font, LEVEL_2, 40, allowCreatingTippedArrows, String.valueOf(config.getMaxTippedArrowsForLevel2()), this::validateAndUpdateMaxTippedArrowsPerLevel)));
        addEntry((level3MaxTippedArrowsEntry = new EditboxEntry(font, LEVEL_3, 40, allowCreatingTippedArrows, String.valueOf(config.getMaxTippedArrowsForLevel3()), this::validateAndUpdateMaxTippedArrowsPerLevel)));

        addEntry(new BlankEntry(font));
        addEntry(new OnOffButtonEntry(font, GENERATE_IN_SWAMP_HUTS, generateInSwampHuts, this::toggleGenerateInSwampHuts));

        String normalPotionChance = ProbabilityUtils.toPercentageString(config.getNormalPotionChance());
        String splashPotionChance = ProbabilityUtils.toPercentageString(config.getSplashPotionChance());
        String lingeringPotionChance = ProbabilityUtils.toPercentageString(config.getLingeringPotionChance());

        addEntry((potionTypesLabelEntry = new LabelEntry(font, POTION_TYPES_PERCENTAGE, 20)));
        addEntry((normalPotionEntry = new EditboxEntry(font, NORMAL_POTION, 40, generateInSwampHuts, normalPotionChance, this::validateAndUpdatePotionTypes)));
        addEntry((splashPotionEntry = new EditboxEntry(font, SPLASH_POTION, 40, generateInSwampHuts, splashPotionChance, this::validateAndUpdatePotionTypes)));
        addEntry((lingeringPotionEntry = new EditboxEntry(font, LINGERING_POTION, 40, generateInSwampHuts, lingeringPotionChance, this::validateAndUpdatePotionTypes)));

        String level1Chance = ProbabilityUtils.toPercentageString(config.getLevel1Chance());
        String level2Chance = ProbabilityUtils.toPercentageString(config.getLevel2Chance());
        String level3Chance = ProbabilityUtils.toPercentageString(config.getLevel3Chance());

        addEntry((potionLevelsLabelEntry = new LabelEntry(font, POTION_LEVELS_PERCENTAGE, 20)));
        addEntry((level1Entry = new EditboxEntry(font, LEVEL_1, 40, generateInSwampHuts, level1Chance, this::validateAndUpdatePotionLevels)));
        addEntry((level2Entry = new EditboxEntry(font, LEVEL_2, 40, generateInSwampHuts, level2Chance, this::validateAndUpdatePotionLevels)));
        addEntry((level3Entry = new EditboxEntry(font, LEVEL_3, 40, generateInSwampHuts, level3Chance, this::validateAndUpdatePotionLevels)));

        addEntry(new LabelEntry(font, POTIONS_PERCENTAGE, 20));
        addEntry(new AddPotionColumnTitlesEntry(font, 40));
        addEntry((addPotionEntry = new AddPotionEntry(font, 40, this::validatePotionEntries, this::addPotionEntry)));

        for (var potionChance : config.potionChances().entrySet())
        {
            String name = potionChance.getKey();
            double chance = potionChance.getValue();

            addEntry(new PotionEntry(font, 40, name, chance, this::removePotionEntry));
        }
    }

    @Override
    protected int getScrollbarPosition()
    {
        return super.getScrollbarPosition() + 50;
    }

    @Override
    public int getRowWidth()
    {
        return 300;
    }

    public boolean isValid()
    {
        return maxTippedArrowsPerLevelLabelEntry.isValid() && potionTypesLabelEntry.isValid() && potionLevelsLabelEntry.isValid();
    }

    public void toggleEvaporatePotionWhenMixed(CycleButton<Boolean> ignored, boolean newValue)
    {
        config.setEvaporatePotionWhenMixed(newValue);
        onChange.run();
    }

    public void toggleAllowMergingPotions(CycleButton<Boolean> ignored, boolean newValue)
    {
        config.setAllowMergingPotions(newValue);
        onChange.run();
    }

    public void toggleApplyPotionEffects(CycleButton<Boolean> ignored, boolean newValue)
    {
        config.setApplyPotionEffectsToEntitiesInside(newValue);
        onChange.run();
    }

    public void toggleAllowFillingWithWaterDrips(CycleButton<Boolean> ignored, boolean newValue)
    {
        config.setAllowFillingWithWaterDrips(newValue);
        onChange.run();
    }

    public void toggleAllowCreatingTippedArrows(CycleButton<Boolean> ignored, boolean newValue)
    {
        config.setAllowCreatingTippedArrows(newValue);

        level1MaxTippedArrowsEntry.enable(newValue);
        level2MaxTippedArrowsEntry.enable(newValue);
        level3MaxTippedArrowsEntry.enable(newValue);

        onChange.run();
    }

    public void toggleGenerateInSwampHuts(CycleButton<Boolean> ignored, boolean newValue)
    {
        config.setGenerateInSwampHuts(newValue);

        normalPotionEntry.enable(newValue);
        splashPotionEntry.enable(newValue);
        lingeringPotionEntry.enable(newValue);

        level1Entry.enable(newValue);
        level2Entry.enable(newValue);
        level3Entry.enable(newValue);

        onChange.run();
    }

    @SuppressWarnings("ExtractMethodRecommender")
    public void validateAndUpdateMaxTippedArrowsPerLevel(String ignored)
    {
        try
        {
            int level1MaxTippedArrows = Integer.parseInt(level1MaxTippedArrowsEntry.getValue());
            int level2MaxTippedArrows = Integer.parseInt(level2MaxTippedArrowsEntry.getValue());
            int level3MaxTippedArrows = Integer.parseInt(level3MaxTippedArrowsEntry.getValue());

            int maxStackSize = Items.TIPPED_ARROW.getMaxStackSize();

            if (level1MaxTippedArrows > maxStackSize || level2MaxTippedArrows > maxStackSize || level3MaxTippedArrows > maxStackSize)
                throw new InvalidConfigValueException("Total exceeds the maximum stack size of %d".formatted(maxStackSize));

            if (level1MaxTippedArrows < 0 || level2MaxTippedArrows < 0 || level3MaxTippedArrows < 0)
                throw new InvalidConfigValueException("Size cannot be less than 0");

            if (level1MaxTippedArrows >= level2MaxTippedArrows || level2MaxTippedArrows >= level3MaxTippedArrows)
                throw new InvalidConfigValueException("Higher cauldron level cannot create lower amount of tipped arrows");

            maxTippedArrowsPerLevelLabelEntry.clearError();

            config.setMaxTippedArrowsForLevel1(level1MaxTippedArrows);
            config.setMaxTippedArrowsForLevel2(level2MaxTippedArrows);
            config.setMaxTippedArrowsForLevel3(level3MaxTippedArrows);
        }
        catch (NumberFormatException | InvalidConfigValueException ex)
        {
            maxTippedArrowsPerLevelLabelEntry.setError(ex instanceof NumberFormatException ? "Invalid number specified" : ex.getMessage());
        }
        finally
        {
            onChange.run();
        }
    }

    public void validateAndUpdatePotionTypes(String ignored)
    {
        try
        {
            BigDecimal normalPotionChance = BigDecimal.valueOf(ProbabilityUtils.toDouble(normalPotionEntry.getValue()));
            BigDecimal splashPotionChance = BigDecimal.valueOf(ProbabilityUtils.toDouble(splashPotionEntry.getValue()));
            BigDecimal lingeringPotionChance = BigDecimal.valueOf(ProbabilityUtils.toDouble(lingeringPotionEntry.getValue()));

            double totalPercentage = normalPotionChance.add(splashPotionChance).add(lingeringPotionChance).doubleValue();
            if (totalPercentage != 1)
                throw new InvalidConfigValueException("Total % must add up to 100%");

            potionTypesLabelEntry.clearError();

            config.setNormalPotionChance(normalPotionChance.doubleValue());
            config.setSplashPotionChance(splashPotionChance.doubleValue());
            config.setLingeringPotionChance(lingeringPotionChance.doubleValue());
        }
        catch (NumberFormatException | InvalidConfigValueException ex)
        {
            potionTypesLabelEntry.setError(ex instanceof NumberFormatException ? "Invalid % specified" : ex.getMessage());
        }
        finally
        {
            onChange.run();
        }
    }

    public void validateAndUpdatePotionLevels(String ignored)
    {
        try
        {
            BigDecimal level1Chance = BigDecimal.valueOf(ProbabilityUtils.toDouble(level1Entry.getValue()));
            BigDecimal level2Chance = BigDecimal.valueOf(ProbabilityUtils.toDouble(level2Entry.getValue()));
            BigDecimal level3Chance = BigDecimal.valueOf(ProbabilityUtils.toDouble(level3Entry.getValue()));

            double totalPercentage = level1Chance.add(level2Chance).add(level3Chance).doubleValue();
            if (totalPercentage != 1)
                throw new InvalidConfigValueException("Total % must add up to 100%");

            potionLevelsLabelEntry.clearError();

            config.setLevel1Chance(level1Chance.doubleValue());
            config.setLevel2Chance(level2Chance.doubleValue());
            config.setLevel3Chance(level3Chance.doubleValue());
        }
        catch (NumberFormatException | InvalidConfigValueException ex)
        {
            potionLevelsLabelEntry.setError(ex instanceof NumberFormatException ? "Invalid % specified" : ex.getMessage());
        }
        finally
        {
            onChange.run();
        }
    }

    public void validatePotionEntries(String ignored)
    {
        try
        {
            String potionName = addPotionEntry.getPotionName();
            Double potionChance = addPotionEntry.getPotionChance();

            if (potionName.isEmpty() || potionChance == null)
            {
                addPotionEntry.clearError();
                addPotionEntry.disableAddButton();

                return;
            }

            Potion potion = Potion.byName(potionName);
            if (potion == Potions.EMPTY)
                throw new InvalidConfigValueException("Invalid potion name specified");

            potionName = potionName.replace("minecraft:", "");
            for (var entry : config.potionChances().entrySet())
            {
                String key = entry.getKey().replace("minecraft:", "");
                if (key.equals(potionName))
                    throw new InvalidConfigValueException("Potion already exists");
            }

            if (potionChance <= 0)
                throw new InvalidConfigValueException("% must be more than 0");

            BigDecimal totalChanceBD = BigDecimal.valueOf(potionChance);
            for (double chance : config.potionChances().values())
                totalChanceBD = totalChanceBD.add(BigDecimal.valueOf(chance));

            double totalChance = totalChanceBD.doubleValue();
            if (totalChance > 1)
            {
                addPotionEntry.setError("Total % for potions exceeds 100%");
                return;
            }

            addPotionEntry.clearError();
        }
        catch (NumberFormatException | InvalidConfigValueException ex)
        {
            addPotionEntry.setError(ex instanceof NumberFormatException ? "Invalid % specified" : ex.getMessage());
        }
    }

    public void addPotionEntry(Button ignored)
    {
        String potionName = addPotionEntry.getPotionName();
        double potionChance = addPotionEntry.getPotionChance();

        addEntry(new PotionEntry(font, 40, potionName, potionChance, this::removePotionEntry));
        config.potionChances().put(potionName, potionChance);

        addPotionEntry.clear();
        onChange.run();
    }

    public void removePotionEntry(PotionEntry entry)
    {
        removeEntryFromTop(entry);
        config.potionChances().remove(entry.getPotionName());

        validatePotionEntries("");
        onChange.run();
    }

    public static abstract class Entry extends ContainerObjectSelectionList.Entry<Entry>
    {
        public static final Component EMPTY = Component.literal("");
        protected static final Component ERROR_ICON = Component.literal("(!)").withStyle(Style.EMPTY.withColor(0xFF0000).applyFormat(ChatFormatting.UNDERLINE));
    }
}
