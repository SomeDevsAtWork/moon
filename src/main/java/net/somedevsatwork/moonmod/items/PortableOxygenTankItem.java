package net.somedevsatwork.moonmod.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.somedevsatwork.moonmod.MoonMod;
import net.somedevsatwork.moonmod.capability.CapabilityOxygen;
import net.somedevsatwork.moonmod.capability.IOxygenStorage;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class PortableOxygenTankItem extends Item {
    public static final ResourceLocation RESOURCE = new ResourceLocation(MoonMod.MODID, "portable_oxygen_tank_item");
    private static final Properties PROPERTIES = new Properties().group(MoonMod.moonModItemGroup).maxStackSize(1);

    public PortableOxygenTankItem() {
        super(PROPERTIES);
    }

    @ParametersAreNonnullByDefault
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        LazyOptional<IOxygenStorage> optionalStorage = stack.getCapability(CapabilityOxygen.OXYGEN);
        if (optionalStorage.isPresent()) {
            IOxygenStorage storage = optionalStorage.orElse(null);
            tooltip.add(new StringTextComponent(String.format("Oxygen Level: %s", storage.getLevel())));
        }
    }
}
