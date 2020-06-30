package net.somedevsatwork.moonmod.dimension;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.somedevsatwork.moonmod.MoonMod;

import java.util.Set;

public class MoonBiomeProvider extends BiomeProvider {
    private static final Set<Biome> biomeList = ImmutableSet.of(MoonMod.moonBiome);

    public MoonBiomeProvider() {
        super(biomeList);
    }

    @Override
    public Biome getNoiseBiome(int x, int y, int z) {
        return MoonMod.moonBiome;
    }
}
