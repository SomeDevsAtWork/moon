package net.somedevsatwork.moonmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class MoonRockBlock extends Block {
    private static final Properties moonRockProperties = Properties.create(Material.ROCK).hardnessAndResistance(1.0f).sound(SoundType.STONE);

    public MoonRockBlock() {
        super(moonRockProperties);
    }
}
