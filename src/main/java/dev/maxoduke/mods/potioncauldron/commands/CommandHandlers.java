package dev.maxoduke.mods.potioncauldron.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

import static net.minecraft.commands.Commands.CommandSelection;
import static net.minecraft.commands.Commands.literal;

public class CommandHandlers
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ignoredContext, CommandSelection environment)
    {
        var reloadConfigCommand = literal(PotionCauldron.MOD_ID).then(literal("config").then(literal("reload").executes(ReloadConfigCommand::handle)));

        if (environment == CommandSelection.DEDICATED)
            reloadConfigCommand.requires(requirements -> requirements.hasPermission(4));

        dispatcher.register(reloadConfigCommand);
    }
}
