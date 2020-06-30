package net.somedevsatwork.moonmod.dimension;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ModDimension;
import net.somedevsatwork.moonmod.MoonMod;

import java.util.function.BiFunction;

public class MoonModDimension extends ModDimension {
    public static final ResourceLocation MOON_MOD_DIMENSION_RESOURCE_LOCATION = new ResourceLocation(MoonMod.MODID, "moonmoddimension");

    @Override
    public BiFunction<World, DimensionType, ? extends Dimension> getFactory() {
        return MoonDimension::new;
    }
}
