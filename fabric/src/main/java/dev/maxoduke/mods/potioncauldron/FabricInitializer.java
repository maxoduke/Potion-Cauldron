package dev.maxoduke.mods.potioncauldron;

import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockEntityRenderer;
import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockInteraction;
import dev.maxoduke.mods.potioncauldron.commands.CommandHandlers;
import dev.maxoduke.mods.potioncauldron.config.ClientConfig;
import dev.maxoduke.mods.potioncauldron.networking.ClientNetworking;
import dev.maxoduke.mods.potioncauldron.networking.ServerNetworking;
import dev.maxoduke.mods.potioncauldron.networking.packets.ParticlePacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class FabricInitializer implements ModInitializer, ClientModInitializer
{
    public static final ResourceLocation CONFIG_CHANNEL = new ResourceLocation(PotionCauldron.MOD_ID, "config_channel");
    public static final ResourceLocation PARTICLES_CHANNEL = new ResourceLocation(PotionCauldron.MOD_ID, "particles_channel");

    static
    {
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(PotionCauldron.MOD_ID, PotionCauldron.BLOCK_NAME), PotionCauldron.BLOCK);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(PotionCauldron.MOD_ID, PotionCauldron.BLOCK_ENTITY_NAME), PotionCauldron.BLOCK_ENTITY);
        Registry.register(BuiltInRegistries.SOUND_EVENT, PotionCauldron.POTION_EVAPORATES_SOUND_ID, PotionCauldron.POTION_EVAPORATES_SOUND_EVENT);
    }

    public void onInitialize()
    {
        PotionCauldronBlockInteraction.bootstrap();

        CommandRegistrationCallback.EVENT.register(CommandHandlers::register);
        ServerLifecycleEvents.SERVER_STARTING.register(ServerNetworking::serverStarting);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> ServerNetworking.serverStopping());
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ServerNetworking.sendConfigToClient(handler.player));
    }

    @Environment(EnvType.CLIENT)
    public void onInitializeClient()
    {
        BlockEntityRenderers.register(PotionCauldron.BLOCK_ENTITY, PotionCauldronBlockEntityRenderer::new);

        ClientPlayNetworking.registerGlobalReceiver(CONFIG_CHANNEL, (client, handler, buf, responseSender) -> ClientNetworking.receiveConfigFromServer(ClientConfig.fromBuf(buf)));
        ClientPlayNetworking.registerGlobalReceiver(PARTICLES_CHANNEL, (client, handler, buf, responseSender) -> ClientNetworking.receiveParticlesFromServer(ParticlePacket.fromBuf(buf)));

        ClientPlayConnectionEvents.DISCONNECT.register(CONFIG_CHANNEL, (handler, client) -> ClientNetworking.clientDisconnected());
    }
}
