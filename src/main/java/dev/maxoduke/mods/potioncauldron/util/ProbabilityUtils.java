package dev.maxoduke.mods.potioncauldron.util;

import java.math.BigDecimal;

public class ProbabilityUtils
{
    public static String toPercentageString(double value)
    {
        return BigDecimal.valueOf(value * 100).stripTrailingZeros().toPlainString();
    }

    public static double toDouble(String value)
    {
        return Double.parseDouble(value.trim()) / 100;
    }
}
