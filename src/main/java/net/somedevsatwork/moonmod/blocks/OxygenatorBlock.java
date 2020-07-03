package net.somedevsatwork.moonmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import net.somedevsatwork.moonmod.tileentities.OxygenatorTileEntity;

import javax.annotation.Nullable;

public class OxygenatorBlock extends Block {
    private static final Properties properties = Properties.create(Material.IRON);

    public OxygenatorBlock() {
        super(properties);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new OxygenatorTileEntity();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}
