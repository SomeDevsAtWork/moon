package net.somedevsatwork.moonmod;

import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.somedevsatwork.moonmod.blocks.MoonRockBlock;
import net.somedevsatwork.moonmod.dimension.MoonBiome;
import net.somedevsatwork.moonmod.dimension.MoonDimension;
import net.somedevsatwork.moonmod.dimension.MoonModDimension;
import net.somedevsatwork.moonmod.items.SpaceBootsItem;
import net.somedevsatwork.moonmod.items.SpaceHelmetItem;
import net.somedevsatwork.moonmod.items.SpaceLeggingsItem;
import net.somedevsatwork.moonmod.items.SpaceSuitItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("moon")
public class MoonMod
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "moon";

    // Blocks
    public static MoonRockBlock moonRockBlock;
    public static BlockItem moonRockBlockItem;

    // Items
    public static SpaceHelmetItem spaceHelmetItem;
    public static SpaceSuitItem spaceSuitItem;
    public static SpaceLeggingsItem spaceLeggingsItem;
    public static SpaceBootsItem spaceBootsItem;

    // Dimension
    public static DimensionType moonDimensionType;
    public static MoonModDimension moonModDimension;
    public static MoonBiome moonBiome;

    public static ItemGroup moonModItemGroup = new ItemGroup("Moon Mod") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(moonRockBlockItem);
        }
    };

    public MoonMod() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {

    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("moon", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event) {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeEvents {
        @SubscribeEvent
        public static void onChunkWatchEvent(ChunkWatchEvent event) {
            DimensionType type = event.getPlayer().world.getDimension().getType();

            if (type == moonDimensionType) {
                // Lower players gravity
                ServerPlayerEntity player = event.getPlayer();
            }
        }

        @SubscribeEvent
        public static void onDimensionRegister(RegisterDimensionsEvent event) {
            moonDimensionType = DimensionManager.registerDimension(MoonModDimension.MOON_MOD_DIMENSION_RESOURCE_LOCATION, moonModDimension, null, true);
        }

        @SubscribeEvent
        public static void onCommand(CommandEvent event) {
            if (!event.getParseResults().getReader().getString().equalsIgnoreCase("/moon")) {
                return;
            }

            CommandSource source = event.getParseResults().getContext().getSource();
            ServerWorld moonWorld = source.getServer().getWorld(moonDimensionType);
            if (!(source.getEntity() instanceof ServerPlayerEntity)) {
                return;
            }

            ServerPlayerEntity player = (ServerPlayerEntity)source.getEntity();

            moonWorld.getChunk(MoonDimension.SPAWN);
            player.teleport(moonWorld, MoonDimension.SPAWN.getX(), MoonDimension.SPAWN.getY(), MoonDimension.SPAWN.getZ(), player.rotationYaw, player.rotationPitch);
        }
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class ModRegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
            moonRockBlock = (MoonRockBlock)new MoonRockBlock().setRegistryName(MODID, "moon_rock");

            // register a new block here
            event.getRegistry().registerAll(moonRockBlock);
        }

        @SubscribeEvent
        public static void onItemsRegister(final RegistryEvent.Register<Item> event) {
            moonRockBlockItem = (BlockItem)new BlockItem(moonRockBlock, new Item.Properties().group(moonModItemGroup)).setRegistryName(MODID, "moon_rock_item");
            spaceHelmetItem = (SpaceHelmetItem)new SpaceHelmetItem().setRegistryName(MODID, "space_helmet_item");
            spaceSuitItem = (SpaceSuitItem)new SpaceSuitItem().setRegistryName(MODID, "space_suit_item");
            spaceLeggingsItem = (SpaceLeggingsItem)new SpaceLeggingsItem().setRegistryName(MODID, "space_leggings_item");
            spaceBootsItem = (SpaceBootsItem) new SpaceBootsItem().setRegistryName(MODID, "space_boots_item");

            event.getRegistry().registerAll(moonRockBlockItem, spaceHelmetItem, spaceSuitItem, spaceLeggingsItem, spaceBootsItem);
        }

        @SubscribeEvent
        public static void onDimensionRegistryEvent(RegistryEvent.Register<ModDimension> event) {
            moonModDimension = (MoonModDimension)new MoonModDimension().setRegistryName(MoonModDimension.MOON_MOD_DIMENSION_RESOURCE_LOCATION);

            event.getRegistry().register(moonModDimension);
        }

        @SubscribeEvent
        public static void onBiomeRegistryEvent(RegistryEvent.Register<Biome> event) {
            moonBiome = (MoonBiome)new MoonBiome().setRegistryName(MODID, "moonbiome");
            event.getRegistry().registerAll(moonBiome);
            BiomeManager.addBiome(MoonBiome.MOON_BIOME_TYPE, new BiomeManager.BiomeEntry(moonBiome, 1));
        }
    }
}
