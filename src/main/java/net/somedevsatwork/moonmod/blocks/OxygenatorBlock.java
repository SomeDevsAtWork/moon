package net.somedevsatwork.moonmod.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class OxygenatorBlock extends Block {
    private static final Properties properties = Properties.create(Material.IRON);

    public OxygenatorBlock() {
        super(properties);
    }
}
