package dev.maxoduke.mods.potioncauldron.config;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;

public class ConfigJsonAdapter implements JsonSerializer<ServerConfig>, JsonDeserializer<ServerConfig>
{
    @Override
    public JsonElement serialize(ServerConfig config, Type type, JsonSerializationContext jsonSerializationContext)
    {
        JsonObject configJson = new JsonObject();

        configJson.addProperty("evaporatePotionWhenMixed", config.shouldEvaporatePotionWhenMixed());
        configJson.addProperty("allowMergingPotions", config.shouldAllowMergingPotions());

        configJson.addProperty("applyPotionEffectsToEntitiesInside", config.shouldApplyPotionEffectsToEntitiesInside());
        configJson.addProperty("potionEffectDurationInSeconds", config.getPotionEffectDurationInSeconds());

        configJson.addProperty("allowCreatingTippedArrows", config.shouldAllowCreatingTippedArrows());

        JsonObject maxTippedArrowsPerLevel = new JsonObject();
        maxTippedArrowsPerLevel.addProperty("level1", config.getMaxTippedArrowsForLevel1());
        maxTippedArrowsPerLevel.addProperty("level2", config.getMaxTippedArrowsForLevel2());
        maxTippedArrowsPerLevel.addProperty("level3", config.getMaxTippedArrowsForLevel3());
        configJson.add("maxTippedArrowsPerLevel", maxTippedArrowsPerLevel);

        configJson.addProperty("generateInSwampHuts", config.shouldGenerateInSwampHuts());

        JsonObject potionTypeChances = new JsonObject();
        potionTypeChances.addProperty("normal", config.getNormalPotionChance());
        potionTypeChances.addProperty("splash", config.getSplashPotionChance());
        potionTypeChances.addProperty("lingering", config.getLingeringPotionChance());
        configJson.add("potionTypeChances", potionTypeChances);

        JsonObject potionLevelChances = new JsonObject();
        potionLevelChances.addProperty("level1", config.getLevel1Chance());
        potionLevelChances.addProperty("level2", config.getLevel2Chance());
        potionLevelChances.addProperty("level3", config.getLevel3Chance());
        configJson.add("potionLevelChances", potionLevelChances);

        JsonArray possiblePotions = new JsonArray();
        for (var entry : config.potionChances().entrySet())
        {
            JsonObject potionWithItsChance = new JsonObject();
            potionWithItsChance.addProperty("name", entry.getKey());
            potionWithItsChance.addProperty("chance", entry.getValue());

            possiblePotions.add(potionWithItsChance);
        }
        configJson.add("potionChances", possiblePotions);

        return configJson;
    }

    @Override
    public ServerConfig deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
    {
        JsonObject configJson = jsonElement.getAsJsonObject();

        ServerConfig config = new ServerConfig();

        config.setEvaporatePotionWhenMixed(configJson.get("evaporatePotionWhenMixed").getAsBoolean());
        config.setAllowMergingPotions(configJson.get("allowMergingPotions").getAsBoolean());

        config.setApplyPotionEffectsToEntitiesInside(configJson.get("applyPotionEffectsToEntitiesInside").getAsBoolean());
        config.setPotionEffectDurationInSeconds(configJson.get("potionEffectDurationInSeconds").getAsInt());

        config.setAllowCreatingTippedArrows(configJson.get("allowCreatingTippedArrows").getAsBoolean());

        JsonObject maxTippedArrowsPerLevel = configJson.getAsJsonObject("maxTippedArrowsPerLevel");
        config.setMaxTippedArrowsForLevel1(maxTippedArrowsPerLevel.get("level1").getAsInt());
        config.setMaxTippedArrowsForLevel2(maxTippedArrowsPerLevel.get("level2").getAsInt());
        config.setMaxTippedArrowsForLevel3(maxTippedArrowsPerLevel.get("level3").getAsInt());

        config.setGenerateInSwampHuts(configJson.get("generateInSwampHuts").getAsBoolean());

        JsonObject potionTypeChances = configJson.getAsJsonObject("potionTypeChances");
        config.setNormalPotionChance(potionTypeChances.get("normal").getAsDouble());
        config.setSplashPotionChance(potionTypeChances.get("splash").getAsDouble());
        config.setLingeringPotionChance(potionTypeChances.get("lingering").getAsDouble());

        JsonObject potionLevelChances = configJson.getAsJsonObject("potionLevelChances");
        config.setLevel1Chance(potionLevelChances.get("level1").getAsDouble());
        config.setLevel2Chance(potionLevelChances.get("level2").getAsDouble());
        config.setLevel3Chance(potionLevelChances.get("level3").getAsDouble());

        HashMap<String, Double> potions = config.potionChances();
        JsonArray possiblePotions = configJson.getAsJsonArray("potionChances");
        for (JsonElement json : possiblePotions)
        {
            JsonObject possiblePotionJson = json.getAsJsonObject();
            String potionName = possiblePotionJson.get("name").getAsString();
            double chance = possiblePotionJson.get("chance").getAsDouble();

            potions.put(potionName, chance);
        }

        return config;
    }
}
