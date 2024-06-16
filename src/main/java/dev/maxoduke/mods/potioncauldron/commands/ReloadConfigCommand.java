package dev.maxoduke.mods.potioncauldron.commands;

import com.mojang.brigadier.context.CommandContext;
import dev.maxoduke.mods.potioncauldron.PotionCauldron;
import dev.maxoduke.mods.potioncauldron.config.ServerConfig;
import dev.maxoduke.mods.potioncauldron.networking.ServerNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ReloadConfigCommand
{
    public static int handle(CommandContext<CommandSourceStack> context)
    {
        boolean shouldExecute = true;

        MinecraftServer server = context.getSource().getServer();
        if (!server.isDedicatedServer())
        {
            var clientPlayer = Minecraft.getInstance().player;
            var serverPlayer = (ServerPlayer) context.getSource().getEntity();

            if (clientPlayer == null || serverPlayer == null)
                return 0;

            shouldExecute = clientPlayer.getUUID().equals(serverPlayer.getUUID());
        }

        if (!shouldExecute)
        {
            context.getSource().sendFailure(Component.literal("Only the host can run this command!"));
            return 0;
        }

        PotionCauldron.CONFIG_MANAGER.setServerConfig(ServerConfig.load());
        ServerNetworking.sendConfigToAllClients();

        context.getSource().sendSystemMessage(Component.literal("Reloading potion cauldron config..."));
        return 1;
    }
}
