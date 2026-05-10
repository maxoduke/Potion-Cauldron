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
        PayloadTypeRegistry.clientboundPlay().register(ClientConfigPayload.TYPE, ClientConfigPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(ParticlePayload.TYPE, ParticlePayload.CODEC);

        CommandRegistrationCallback.EVENT.register(CommandHandlers::register);
        ServerLifecycleEvents.SERVER_STARTING.register(ServerNetworking::serverStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(ignoredServer -> ServerNetworking.serverStopping());
        ServerPlayConnectionEvents.JOIN.register((handler, ignoredSender, ignoredServer) -> ServerNetworking.sendConfigToClient(handler.player));
    }

    @Environment(EnvType.CLIENT)
    public void onInitializeClient()
    {
        BlockEntityRenderers.register(PotionCauldron.BLOCK_ENTITY, PotionCauldronBlockEntityRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(ClientConfigPayload.TYPE, (payload, ignoredContext) -> ClientNetworking.receiveConfigFromServer(payload));
        ClientPlayNetworking.registerGlobalReceiver(ParticlePayload.TYPE, (payload, ignoredContext) -> ClientNetworking.receiveParticlesFromServer(payload));

        ClientPlayConnectionEvents.DISCONNECT.register(PotionCauldron.CONFIG_CHANNEL, (ignoredHandler, ignoredClient) -> ClientNetworking.clientDisconnected());
    }
}
