package net.somedevsatwork.moonmod.dimension;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.common.BiomeManager;
import net.somedevsatwork.moonmod.MoonMod;

public class MoonBiome extends Biome {
    private static final SurfaceBuilderConfig SURFACE_BUILDER_CONFIG = new SurfaceBuilderConfig(
            MoonMod.moonRockBlock.getDefaultState(), MoonMod.moonRockBlock.getDefaultState(), MoonMod.moonRockBlock.getDefaultState());
    private static final Builder moonBiomeBuilder = new Builder()
            .surfaceBuilder(SurfaceBuilder.DEFAULT, SURFACE_BUILDER_CONFIG)
            .precipitation(RainType.NONE)
            .category(Category.DESERT)
            .depth(1.0f)
            .scale(1.0f)
            .temperature(1.0f)
            .downfall(1.0f)
            .waterColor(MathHelper.rgb(0f, 0f, 1f))
            .waterFogColor(MathHelper.rgb(0f, 0f, 0f));

    public static final BiomeManager.BiomeType MOON_BIOME_TYPE = BiomeManager.BiomeType.ICY;

    public MoonBiome() {
        super(moonBiomeBuilder);
    }

    @Override
    public int getSkyColor() {
        return MathHelper.rgb(0f, 0f, 0f);
    }
}
