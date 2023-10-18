package dev.maxoduke.mods.potioncauldron.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;

import static net.minecraft.commands.Commands.CommandSelection;
import static net.minecraft.commands.Commands.literal;

@SuppressWarnings("SpellCheckingInspection")
public class CommandHandlers
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ignoredContext, CommandSelection environment)
    {
        var reloadConfigCommand = literal("potioncauldron").then(literal("config").then(literal("reload").executes(ReloadConfigCommand::handle)));

        if (environment.includeDedicated)
            reloadConfigCommand.requires(requirements -> requirements.hasPermission(4));

        dispatcher.register(reloadConfigCommand);
    }
}
