package net.somedevsatwork.moonmod.dimension;

import net.minecraft.world.gen.GenerationSettings;
import net.somedevsatwork.moonmod.MoonMod;

public class MoonGenerationSettings extends GenerationSettings {
    public MoonGenerationSettings() {
        setDefaultBlock(MoonMod.moonRockBlock.getDefaultState());
    }

    public int getBedrockFloorHeight() {
        return 0;
    }
}
