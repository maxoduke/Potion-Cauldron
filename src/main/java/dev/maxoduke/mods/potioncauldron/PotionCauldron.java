package dev.maxoduke.mods.potioncauldron;

import dev.maxoduke.mods.potioncauldron.block.*;
import dev.maxoduke.mods.potioncauldron.commands.CommandHandlers;
import dev.maxoduke.mods.potioncauldron.config.ConfigManager;
import dev.maxoduke.mods.potioncauldron.config.gui.ConfigScreen;
import dev.maxoduke.mods.potioncauldron.networking.ClientNetworking;
import dev.maxoduke.mods.potioncauldron.networking.NetworkHandler;
import dev.maxoduke.mods.potioncauldron.networking.ServerNetworking;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

@SuppressWarnings({ "SpellCheckingInspection", "unused" })
@Mod(PotionCauldron.MOD_ID)
public class PotionCauldron
{
    private static FMLJavaModLoadingContext context;

    public static final String MOD_ID = "potioncauldron";
    public static final String MOD_NAME = "Potion Cauldron";
    public static final Logger LOG = LogManager.getLogger(MOD_NAME);

    public static final String BLOCK_NAME = "potion_cauldron";
    public static final String BLOCK_ENTITY_NAME = "potion_cauldron_block_entity";
    public static final String POTION_EVAPORATES_SOUND_NAME = "potion_evaporates";

    public static final Identifier POTION_EVAPORATES_SOUND_ID = Identifier.fromNamespaceAndPath(MOD_ID, POTION_EVAPORATES_SOUND_NAME);
    public static final Identifier CONFIG_CHANNEL = Identifier.fromNamespaceAndPath(PotionCauldron.MOD_ID, "config_channel");
    public static final Identifier PARTICLES_CHANNEL = Identifier.fromNamespaceAndPath(PotionCauldron.MOD_ID, "particles_channel");

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PotionCauldron.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PotionCauldron.MOD_ID);
    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PotionCauldron.MOD_ID);

    public static final RegistryObject<PotionCauldronBlock> BLOCK = BLOCKS.register(
        BLOCK_NAME,
        () -> new PotionCauldronBlock(
            Biome.Precipitation.RAIN,
            PotionCauldronBlockInteractions.POTION,
            BlockBehaviour.Properties
                .ofFullCopy(Blocks.CAULDRON)
                .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, BLOCK_NAME)))
        )
    );

    public static final RegistryObject<BlockEntityType<@NotNull PotionCauldronBlockEntity>> BLOCK_ENTITY = BLOCK_ENTITIES.register(
        BLOCK_ENTITY_NAME,
        () -> new BlockEntityType<>(PotionCauldronBlockEntity::new, Set.of(BLOCK.get()))
    );

    public static final RegistryObject<SoundEvent> POTION_EVAPORATES_SOUND_EVENT = SOUND_EVENTS.register(
        POTION_EVAPORATES_SOUND_NAME,
        () -> SoundEvent.createVariableRangeEvent(POTION_EVAPORATES_SOUND_ID)
    );

    public static final ConfigManager CONFIG_MANAGER = new ConfigManager();

    public PotionCauldron(FMLJavaModLoadingContext context)
    {
        PotionCauldron.context = context;
        PotionCauldronBlockInteractions.bootstrap();

        NetworkHandler.register();

        BusGroup modBusGroup = context.getModBusGroup();
        BLOCKS.register(modBusGroup);
        BLOCK_ENTITIES.register(modBusGroup);
        SOUND_EVENTS.register(modBusGroup);

        EntityRenderersEvent.RegisterRenderers.BUS.addListener(this::registerBlockEntityRenderers);
        RegisterCommandsEvent.BUS.addListener(this::registerCommands);
        ServerStartingEvent.BUS.addListener(this::serverStarting);
        ServerStoppingEvent.BUS.addListener(this::serverStopping);
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(this::playerJoined);
    }

    @Mod.EventBusSubscriber(modid = PotionCauldron.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            context.registerExtensionPoint(
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
    public void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerBlockEntityRenderer(PotionCauldron.BLOCK_ENTITY.get(), PotionCauldronBlockEntityRenderer::new);
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
