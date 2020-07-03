package net.somedevsatwork.moonmod;

import net.minecraft.block.Block;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
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
import net.somedevsatwork.moonmod.blocks.OxygenatorBlock;
import net.somedevsatwork.moonmod.capability.CapabilityOxygen;
import net.somedevsatwork.moonmod.capability.IOxygenStorage;
import net.somedevsatwork.moonmod.capability.OxygenStorage;
import net.somedevsatwork.moonmod.capability.OxygenatedRegion;
import net.somedevsatwork.moonmod.dimension.MoonBiome;
import net.somedevsatwork.moonmod.dimension.MoonDimension;
import net.somedevsatwork.moonmod.dimension.MoonModDimension;
import net.somedevsatwork.moonmod.items.*;
import net.somedevsatwork.moonmod.tileentities.OxygenatorTileEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("moon")
public class MoonMod {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private static final int OXYGEN_EXPENSE_TICK = 40;

    public static final String MODID = "moon";

    // Blocks
    public static MoonRockBlock moonRockBlock;
    public static BlockItem moonRockBlockItem;
    public static OxygenatorBlock oxygenatorBlock;
    public static BlockItem oxygenatorBlockItem;

    // Items
    public static SpaceHelmetItem spaceHelmetItem;
    public static SpaceSuitItem spaceSuitItem;
    public static SpaceLeggingsItem spaceLeggingsItem;
    public static SpaceBootsItem spaceBootsItem;
    public static PortableOxygenTankItem portableOxygenTankItem;

    // Dimension
    public static DimensionType moonDimensionType;
    public static MoonModDimension moonModDimension;
    public static MoonBiome moonBiome;

    /**
     * Map of all oxygenated regions
     * Key: Region's hashcode
     * Value: region
     */
    public static ConcurrentHashMap<Integer, OxygenatedRegion> oxygenRegions;

    public static ItemGroup moonModItemGroup = new ItemGroup("moon") {
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

        oxygenRegions = new ConcurrentHashMap<>();
    }

    private void setup(final FMLCommonSetupEvent event) {
        CapabilityOxygen.register();
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
            moonDimensionType = DimensionManager.registerOrGetDimension(MoonModDimension.MOON_MOD_DIMENSION_RESOURCE_LOCATION, moonModDimension, null, true);
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

        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            // Clear out empty regions
            oxygenRegions.keySet().removeIf(key -> oxygenRegions.get(key).getOxygenStorage().getLevel() == 0);
        }

        @SubscribeEvent
        public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
            if (event.player.world.getDimension().getType() != moonDimensionType) {
                return;
            }

            boolean fullSuitOn = MoonHelper.hasFullSuitOn(event.player);
            OxygenatedRegion occupiedOxygenatedRegion = MoonHelper.inOxygenatedArea(event.player);

            // Check if they should be suffocating
            if (occupiedOxygenatedRegion != null) {
                IOxygenStorage regionOxygenStore = occupiedOxygenatedRegion.getOxygenStorage();

                // Attempt to breathe from region
                int breathedOxygen = regionOxygenStore.extract(1);
                if (breathedOxygen == 0) {
                    // Region became deoxygenated
                    return;
                }

                // Attempt to fill oxygenatable items in inventory
                event.player.inventory.mainInventory
                        .stream()
                        .filter(MoonHelper::hasOxygenCapability)
                        .map(stack -> stack.getCapability(CapabilityOxygen.OXYGEN).orElse(null))
                        .filter(storage -> !storage.isFull())
                        .forEach(storage -> {
                            int oxygen = regionOxygenStore.extract(1);
                            storage.accept(oxygen);
                        });
            } else {
                if (fullSuitOn) {
                    // Check if I have any items that have oxygen that can be consumed
                    if (!MoonHelper.extractOxygen(event.player, 1)) {
                        // Sufficate
                        LOGGER.debug("Unoxygenated area + suit + couldn't consume");
                    } else {
                        LOGGER.debug("Unoxygenated area + suit + consume");
                    }
                } else {
                    // Sufficate
                    LOGGER.debug("Unoxygenated area + no suit");
                }
            }
        }

        @SubscribeEvent
        public static void onAttachCapabilitiesItemStack(AttachCapabilitiesEvent<ItemStack> event) {
            ItemStack te = event.getObject();
            if (te.getItem() == portableOxygenTankItem) {
                event.addCapability(PortableOxygenTankItem.RESOURCE, new CapabilityOxygen.Provider());
            }
        }

        @SubscribeEvent
        public static void onAttachCapabilitiesEntity(AttachCapabilitiesEvent<Entity> event) {
            event.addCapability(CapabilityOxygen.PLAYER_RESOURCE, new CapabilityOxygen.Provider());
        }
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class ModRegistryEvents {
        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
            moonRockBlock = (MoonRockBlock)new MoonRockBlock().setRegistryName(MODID, "moon_rock");
            oxygenatorBlock = (OxygenatorBlock)new OxygenatorBlock().setRegistryName(MODID, "oxygenator");

            // register a new block here
            event.getRegistry().registerAll(moonRockBlock, oxygenatorBlock);
        }

        @SubscribeEvent
        public static void onItemsRegister(final RegistryEvent.Register<Item> event) {
            moonRockBlockItem = (BlockItem)new BlockItem(moonRockBlock, new Item.Properties().group(moonModItemGroup)).setRegistryName(MODID, "moon_rock_item");
            oxygenatorBlockItem = (BlockItem)new BlockItem(oxygenatorBlock, new Item.Properties().group(moonModItemGroup)).setRegistryName(MODID, "oxygenator_item");
            spaceHelmetItem = (SpaceHelmetItem)new SpaceHelmetItem().setRegistryName(MODID, "space_helmet_item");
            spaceSuitItem = (SpaceSuitItem)new SpaceSuitItem().setRegistryName(MODID, "space_suit_item");
            spaceLeggingsItem = (SpaceLeggingsItem)new SpaceLeggingsItem().setRegistryName(MODID, "space_leggings_item");
            spaceBootsItem = (SpaceBootsItem) new SpaceBootsItem().setRegistryName(MODID, "space_boots_item");
            portableOxygenTankItem = (PortableOxygenTankItem)new PortableOxygenTankItem().setRegistryName(MODID, "portable_oxygen_tank_item");

            event.getRegistry().registerAll(moonRockBlockItem, oxygenatorBlockItem, spaceHelmetItem, spaceSuitItem, spaceLeggingsItem, spaceBootsItem, portableOxygenTankItem);
        }

        @SubscribeEvent
        public static void onTileEntityRegister(final RegistryEvent.Register<TileEntityType<?>> event) {
            OxygenatorTileEntity.OXYGENATOR_TILE_ENTITY_TYPE.setRegistryName(MoonMod.MODID, "oxygenator_te");

            event.getRegistry().registerAll(OxygenatorTileEntity.OXYGENATOR_TILE_ENTITY_TYPE);
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

    public static class MoonHelper {
        public static boolean isMoon(World world) {
            return world != null && world.getDimension().getType() == moonDimensionType;
        }

        public static boolean hasFullSuitOn(PlayerEntity player) {
            AtomicBoolean hasFullSuitOn = new AtomicBoolean(true);
            player.getArmorInventoryList().forEach(stack -> {
                if (stack == ItemStack.EMPTY) {
                    hasFullSuitOn.set(false);
                }
            });

            return hasFullSuitOn.get();
        }

        public static boolean hasOxygenCapability(ItemStack stack) {
            return stack.getCapability(CapabilityOxygen.OXYGEN).isPresent();
        }

        @Nullable
        public static OxygenatedRegion inOxygenatedArea(PlayerEntity player) {
            for (OxygenatedRegion region : oxygenRegions.values()) {
                if (region.positionInRegion(player.getPosition())) {
                    LOGGER.debug("Player in region with oxygen level: {}", region.getOxygenStorage().getLevel());
                    return region;
                }
            }

            return null;
        }

        /**
         * Takes oxygen from a player and returns whether the player gave the oxygen amount
         * @param player the player to extract it from
         * @param amount the amount to attempt to extract
         * @return whether the oxygen was fully extracted
         */
        public static boolean extractOxygen(PlayerEntity player, int amount) {
            List<IOxygenStorage> storages = player.inventory.mainInventory
                    .stream()
                    .filter(MoonHelper::hasOxygenCapability)
                    .map(stack -> stack.getCapability(CapabilityOxygen.OXYGEN).orElse(null))
                    .collect(Collectors.toList());

            // Attempt to extract oxygen from 1 of the available sources
            for (IOxygenStorage storage : storages) {
                int notExtracted = storage.extract(amount);
                if (notExtracted != 0) {
                    return true;
                }
            }

            return false;
        }
    }
}
