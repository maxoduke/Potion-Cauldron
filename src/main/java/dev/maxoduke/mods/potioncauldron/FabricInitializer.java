package dev.maxoduke.mods.potioncauldron;

import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockEntityRenderer;
import dev.maxoduke.mods.potioncauldron.commands.CommandHandlers;
import dev.maxoduke.mods.potioncauldron.networking.ClientNetworking;
import dev.maxoduke.mods.potioncauldron.networking.ServerNetworking;
import dev.maxoduke.mods.potioncauldron.networking.payloads.ClientConfigPayload;
import dev.maxoduke.mods.potioncauldron.networking.payloads.ParticlePayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class FabricInitializer implements ModInitializer, ClientModInitializer
{
    public void onInitialize()
    {
        PayloadTypeRegistry.playS2C().register(ClientConfigPayload.TYPE, ClientConfigPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(ParticlePayload.TYPE, ParticlePayload.CODEC);

        CommandRegistrationCallback.EVENT.register(CommandHandlers::register);
        ServerLifecycleEvents.SERVER_STARTING.register(ServerNetworking::serverStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ServerNetworking.serverStopping());
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ServerNetworking.sendConfigToClient(handler.player));
    }

    @Environment(EnvType.CLIENT)
    public void onInitializeClient()
    {
        BlockEntityRenderers.register(PotionCauldron.BLOCK_ENTITY, PotionCauldronBlockEntityRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(ClientConfigPayload.TYPE, (payload, context) -> ClientNetworking.receiveConfigFromServer(payload));
        ClientPlayNetworking.registerGlobalReceiver(ParticlePayload.TYPE, (payload, context) -> ClientNetworking.receiveParticlesFromServer(payload));

        ClientPlayConnectionEvents.DISCONNECT.register(PotionCauldron.CONFIG_CHANNEL, (handler, client) -> ClientNetworking.clientDisconnected());
    }
}
