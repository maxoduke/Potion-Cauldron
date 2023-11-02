package dev.maxoduke.mods.potioncauldron;

import dev.maxoduke.mods.potioncauldron.block.CauldronInteractionInjector;
import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockEntityRenderer;
import dev.maxoduke.mods.potioncauldron.block.PotionCauldronBlockInteraction;
import dev.maxoduke.mods.potioncauldron.commands.CommandHandlers;
import dev.maxoduke.mods.potioncauldron.config.gui.ConfigScreen;
import dev.maxoduke.mods.potioncauldron.networking.ClientNetworking;
import dev.maxoduke.mods.potioncauldron.networking.NetworkHandler;
import dev.maxoduke.mods.potioncauldron.networking.ServerNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(PotionCauldron.MOD_ID)
public class ForgeInitializer
{
    private static final DeferredRegister<Block> BLOCKS;
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES;
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS;

    static
    {
        BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PotionCauldron.MOD_ID);
        BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PotionCauldron.MOD_ID);
        SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PotionCauldron.MOD_ID);

        BLOCKS.register(PotionCauldron.BLOCK_NAME, () -> PotionCauldron.BLOCK);
        BLOCK_ENTITIES.register(PotionCauldron.BLOCK_ENTITY_NAME, () -> PotionCauldron.BLOCK_ENTITY);
        SOUND_EVENTS.register(PotionCauldron.POTION_EVAPORATES_SOUND_NAME, () -> PotionCauldron.POTION_EVAPORATES_SOUND_EVENT);
    }

    public ForgeInitializer()
    {
        CauldronInteractionInjector.injectIntoEmptyPotionInteraction();
        PotionCauldronBlockInteraction.bootstrap();

        NetworkHandler.register();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);
        SOUND_EVENTS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::registerBlockEntityRenderes);
    }

    @Mod.EventBusSubscriber(modid = PotionCauldron.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            ModLoadingContext.get().registerExtensionPoint(
                ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> ConfigScreen.create(screen))
            );
        }
    }

    @Mod.EventBusSubscriber(modid = PotionCauldron.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientModNetworkEvents
    {
        @SubscribeEvent
        public static void clientDisconnected(ClientPlayerNetworkEvent.LoggingOut event)
        {
            ClientNetworking.clientDisconnected();
        }
    }

    @SubscribeEvent
    public void registerBlockEntityRenderes(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(PotionCauldron.BLOCK_ENTITY, PotionCauldronBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        CommandHandlers.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    @SubscribeEvent
    public void serverStarting(ServerStartingEvent event)
    {
        ServerNetworking.serverStarting(event.getServer());
    }

    @SubscribeEvent
    public void serverStopping(ServerStoppingEvent event)
    {
        ServerNetworking.serverStopping();
    }

    @SubscribeEvent
    public void playerJoined(PlayerEvent.PlayerLoggedInEvent event)
    {
        ServerPlayer player = (ServerPlayer) event.getEntity();
        ServerNetworking.sendConfigToClient(player);
    }
}
