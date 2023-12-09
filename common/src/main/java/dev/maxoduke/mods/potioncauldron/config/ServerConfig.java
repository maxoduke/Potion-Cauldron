package dev.maxoduke.mods.potioncauldron.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.config.exceptions.ConfigNotFoundException;
import dev.maxoduke.mods.potioncauldron.config.exceptions.InvalidConfigException;
import dev.maxoduke.mods.potioncauldron.platform.Services;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;

public class ServerConfig implements IConfig
{
    public static final String CONFIG_FILE_NAME;
    public static final Path CONFIG_FILE_PATH;
    private static final Gson GSON;

    private boolean evaporatePotionWhenMixed;
    private boolean allowMergingPotions;

    private boolean applyPotionEffectsToEntitiesInside;

    private boolean allowFillingWithWaterDrips;

    private boolean allowCreatingTippedArrows;
    private int maxTippedArrowsForLevel1;
    private int maxTippedArrowsForLevel2;
    private int maxTippedArrowsForLevel3;

    private boolean generateInSwampHuts;
    private double normalPotionChance;
    private double splashPotionChance;
    private double lingeringPotionChance;
    private double level1Chance;
    private double level2Chance;
    private double level3Chance;

    private final HashMap<String, Double> _potionChances;

    static
    {
        CONFIG_FILE_NAME = "potion-cauldron.json";
        CONFIG_FILE_PATH = Path.of(Services.PLATFORM.getConfigDirPath(), CONFIG_FILE_NAME);

        GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(ServerConfig.class, new ConfigJsonAdapter())
            .setPrettyPrinting()
            .create();
    }

    public ServerConfig()
    {
        _potionChances = new HashMap<>();
    }

    @NotNull
    public ClientConfig asClientConfig()
    {
        ClientConfig clientConfig = new ClientConfig(evaporatePotionWhenMixed, allowMergingPotions, allowCreatingTippedArrows);

        String json = GSON.toJson(clientConfig, ClientConfig.class);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        buf.writeByteArray(json.getBytes(StandardCharsets.UTF_8));
        return clientConfig;
    }

    public boolean shouldEvaporatePotionWhenMixed() { return evaporatePotionWhenMixed; }

    public void setEvaporatePotionWhenMixed(boolean evaporatePotionWhenMixed) { this.evaporatePotionWhenMixed = evaporatePotionWhenMixed; }

    public boolean shouldAllowMergingPotions() { return allowMergingPotions; }

    public void setAllowMergingPotions(boolean allowMergingPotions) { this.allowMergingPotions = allowMergingPotions; }

    public boolean shouldApplyPotionEffectsToEntitiesInside() { return applyPotionEffectsToEntitiesInside; }

    public void setApplyPotionEffectsToEntitiesInside(boolean value) { applyPotionEffectsToEntitiesInside = value; }

    public boolean shouldAllowFillingWithWaterDrips() { return allowFillingWithWaterDrips; }

    public void setAllowFillingWithWaterDrips(boolean value) { allowFillingWithWaterDrips = value; }

    public boolean shouldAllowCreatingTippedArrows() { return allowCreatingTippedArrows; }

    public void setAllowCreatingTippedArrows(boolean allowCreatingTippedArrows) { this.allowCreatingTippedArrows = allowCreatingTippedArrows; }

    public int getMaxTippedArrowsForLevel1() { return maxTippedArrowsForLevel1; }

    public void setMaxTippedArrowsForLevel1(int maxTippedArrowsForLevel1) { this.maxTippedArrowsForLevel1 = maxTippedArrowsForLevel1; }

    public int getMaxTippedArrowsForLevel2() { return maxTippedArrowsForLevel2; }

    public void setMaxTippedArrowsForLevel2(int maxTippedArrowsForLevel2) { this.maxTippedArrowsForLevel2 = maxTippedArrowsForLevel2; }

    public int getMaxTippedArrowsForLevel3() { return maxTippedArrowsForLevel3; }

    public void setMaxTippedArrowsForLevel3(int maxTippedArrowsForLevel3) { this.maxTippedArrowsForLevel3 = maxTippedArrowsForLevel3; }

    public boolean shouldGenerateInSwampHuts() { return generateInSwampHuts; }

    public void setGenerateInSwampHuts(boolean value) { generateInSwampHuts = value; }

    public double getNormalPotionChance() { return normalPotionChance; }

    public void setNormalPotionChance(double normalPotionChance) { this.normalPotionChance = normalPotionChance; }

    public double getSplashPotionChance() { return splashPotionChance; }

    public void setSplashPotionChance(double splashPotionChance) { this.splashPotionChance = splashPotionChance; }

    public double getLingeringPotionChance() { return lingeringPotionChance; }

    public void setLingeringPotionChance(double lingeringPotionChance) { this.lingeringPotionChance = lingeringPotionChance; }

    public double getLevel1Chance() { return level1Chance; }

    public void setLevel1Chance(double level1Chance) { this.level1Chance = level1Chance; }

    public double getLevel2Chance() { return level2Chance; }

    public void setLevel2Chance(double level2Chance) { this.level2Chance = level2Chance; }

    public double getLevel3Chance() { return level3Chance; }

    public void setLevel3Chance(double level3Chance) { this.level3Chance = level3Chance; }

    public HashMap<Integer, Integer> maxTippedArrowsPerLevel()
    {
        HashMap<Integer, Integer> maxTippedArrowsPerLevel = new HashMap<>();
        maxTippedArrowsPerLevel.put(1, maxTippedArrowsForLevel1);
        maxTippedArrowsPerLevel.put(2, maxTippedArrowsForLevel2);
        maxTippedArrowsPerLevel.put(3, maxTippedArrowsForLevel3);

        return maxTippedArrowsPerLevel;
    }

    public HashMap<String, Double> potionTypeChances()
    {
        HashMap<String, Double> possiblePotionTypes = new HashMap<>();
        possiblePotionTypes.put("minecraft:potion", normalPotionChance);
        possiblePotionTypes.put("minecraft:splash_potion", splashPotionChance);
        possiblePotionTypes.put("minecraft:lingering_potion", lingeringPotionChance);

        return possiblePotionTypes;
    }

    public HashMap<Integer, Double> potionLevelChances()
    {
        HashMap<Integer, Double> possiblePotionLevels = new HashMap<>();
        possiblePotionLevels.put(1, level1Chance);
        possiblePotionLevels.put(2, level2Chance);
        possiblePotionLevels.put(3, level3Chance);

        return possiblePotionLevels;
    }

    public HashMap<String, Double> potionChances() { return _potionChances; }

    private double validateAndGetTotalChance(Collection<Double> values, boolean allowZeroIndividualChance)
    {
        BigDecimal totalBD = BigDecimal.valueOf(0.0);
        for (double chance : values)
        {
            if (chance < 0 || (!allowZeroIndividualChance && chance == 0) || chance > 1)
                throw new InvalidConfigException();

            totalBD = totalBD.add(BigDecimal.valueOf(chance));
        }

        double total = totalBD.doubleValue();
        if (total < 0 || total > 1)
            throw new InvalidConfigException();

        return total;
    }

    public void throwIfInvalid()
    {
        validateAndGetTotalChance(potionChances().values(), false);
        double totalPotionTypeChance = validateAndGetTotalChance(potionTypeChances().values(), true);
        double totalPotionLevelChance = validateAndGetTotalChance(potionLevelChances().values(), true);

        if (totalPotionTypeChance != 1 || totalPotionLevelChance != 1)
            throw new InvalidConfigException();
        else if (maxTippedArrowsForLevel1 >= maxTippedArrowsForLevel2 || maxTippedArrowsForLevel2 >= maxTippedArrowsForLevel3)
            throw new InvalidConfigException();
    }

    public boolean notEquals(ServerConfig that)
    {
        return this.evaporatePotionWhenMixed != that.evaporatePotionWhenMixed ||
            this.allowMergingPotions != that.allowMergingPotions ||
            this.applyPotionEffectsToEntitiesInside != that.applyPotionEffectsToEntitiesInside ||
            this.allowFillingWithWaterDrips != that.allowFillingWithWaterDrips ||
            this.allowCreatingTippedArrows != that.allowCreatingTippedArrows ||
            this.maxTippedArrowsForLevel1 != that.maxTippedArrowsForLevel1 ||
            this.maxTippedArrowsForLevel2 != that.maxTippedArrowsForLevel2 ||
            this.maxTippedArrowsForLevel3 != that.maxTippedArrowsForLevel3 ||
            this.generateInSwampHuts != that.generateInSwampHuts ||
            this.normalPotionChance != that.normalPotionChance ||
            this.splashPotionChance != that.splashPotionChance ||
            this.lingeringPotionChance != that.lingeringPotionChance ||
            this.level1Chance != that.level1Chance ||
            this.level2Chance != that.level2Chance ||
            this.level3Chance != that.level3Chance ||
            !this._potionChances.equals(that._potionChances);
    }

    public ServerConfig copy()
    {
        ServerConfig copy = new ServerConfig();

        copy.evaporatePotionWhenMixed = evaporatePotionWhenMixed;
        copy.allowMergingPotions = allowMergingPotions;

        copy.applyPotionEffectsToEntitiesInside = applyPotionEffectsToEntitiesInside;
        copy.allowFillingWithWaterDrips = allowFillingWithWaterDrips;

        copy.allowCreatingTippedArrows = allowCreatingTippedArrows;
        copy.maxTippedArrowsForLevel1 = maxTippedArrowsForLevel1;
        copy.maxTippedArrowsForLevel2 = maxTippedArrowsForLevel2;
        copy.maxTippedArrowsForLevel3 = maxTippedArrowsForLevel3;

        copy.generateInSwampHuts = generateInSwampHuts;
        copy.normalPotionChance = normalPotionChance;
        copy.splashPotionChance = splashPotionChance;
        copy.lingeringPotionChance = lingeringPotionChance;

        copy.level1Chance = level1Chance;
        copy.level2Chance = level2Chance;
        copy.level3Chance = level3Chance;

        copy._potionChances.putAll(_potionChances);
        return copy;
    }

    public void save()
    {
        try
        {
            String jsonData = GSON.toJson(this, ServerConfig.class);
            Files.writeString(CONFIG_FILE_PATH, jsonData);
        }
        catch (Exception ex)
        {
            PotionCauldron.LOG.error(String.format("Error saving Potion Cauldron config: %s", ex.getMessage()));
        }
    }

    @NotNull
    public static ServerConfig load()
    {
        try
        {
            if (!Files.exists(CONFIG_FILE_PATH))
                throw new ConfigNotFoundException();

            String jsonData = Files.readString(CONFIG_FILE_PATH);
            ServerConfig loadedConfig = GSON.fromJson(jsonData, ServerConfig.class);

            if (loadedConfig == null)
                throw new InvalidConfigException();

            loadedConfig.throwIfInvalid();
            return loadedConfig;
        }
        catch (Exception ex)
        {
            PotionCauldron.LOG.error(
                ex instanceof NullPointerException || ex instanceof JsonParseException
                    ? new InvalidConfigException().getMessage()
                    : ex.getMessage()
            );

            ServerConfig config = ServerConfig.createDefault();
            config.save();

            return config;
        }
    }

    public static ServerConfig createDefault()
    {
        ServerConfig config = new ServerConfig();

        config.evaporatePotionWhenMixed = true;
        config.allowMergingPotions = true;

        config.applyPotionEffectsToEntitiesInside = false;

        // Cauldron Level 1 = 16 tipped arrows
        // Cauldron Level 2 = 32 tipped arrows
        // Cauldron Level 3 = 64 tipped arrows
        // ^ taken from Bedrock
        config.allowCreatingTippedArrows = true;
        config.maxTippedArrowsForLevel1 = 16;
        config.maxTippedArrowsForLevel2 = 32;
        config.maxTippedArrowsForLevel3 = 64;

        config.generateInSwampHuts = true;

        config.normalPotionChance = 0.75;
        config.splashPotionChance = 0.25;
        config.lingeringPotionChance = 0.0;

        config.level1Chance = 0.5;
        config.level2Chance = 0.3;
        config.level3Chance = 0.2;

        config._potionChances.put("minecraft:fire_resistance", 0.05);
        config._potionChances.put("minecraft:slowness", 0.1);
        config._potionChances.put("minecraft:weakness", 0.1);
        config._potionChances.put("minecraft:strength", 0.1);
        config._potionChances.put("minecraft:water_breathing", 0.1);
        config._potionChances.put("minecraft:swiftness", 0.15);
        config._potionChances.put("minecraft:healing", 0.2);
        config._potionChances.put("minecraft:poison", 0.2);

        return config;
    }
}
