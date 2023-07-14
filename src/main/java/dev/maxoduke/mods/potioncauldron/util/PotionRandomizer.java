package dev.maxoduke.mods.potioncauldron.util;

import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import net.minecraft.util.RandomSource;

import java.util.HashMap;

public class PotionRandomizer
{
    private static <T> T getRandomKeyByItsProbability(HashMap<T, Double> map)
    {
        RandomSource random = RandomSource.create();

        double randomValue = random.nextDouble();
        double cumulativeProbability = 0.0;

        T result = null;
        for (var possibleResult : map.entrySet())
        {
            cumulativeProbability += possibleResult.getValue();
            if (randomValue <= cumulativeProbability)
            {
                result = possibleResult.getKey();
                break;
            }
        }

        return result;
    }

    public static String getRandomPotion()
    {
        return getRandomKeyByItsProbability(PotionCauldron.CONFIG_MANAGER.serverConfig().potionChances());
    }

    public static String getRandomPotionType()
    {
        return getRandomKeyByItsProbability(PotionCauldron.CONFIG_MANAGER.serverConfig().potionTypeChances());
    }

    public static Integer getRandomPotionLevel()
    {
        return getRandomKeyByItsProbability(PotionCauldron.CONFIG_MANAGER.serverConfig().potionLevelChances());
    }
}
