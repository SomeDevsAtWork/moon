package net.somedevsatwork.moonmod.tileentities;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.somedevsatwork.moonmod.MoonMod;
import net.somedevsatwork.moonmod.algorithms.EnclosedSpaceAlgorithm;
import net.somedevsatwork.moonmod.capability.OxygenatedRegion;

public class OxygenatorTileEntity extends TileEntity implements ITickableTileEntity {
    public static final TileEntityType<OxygenatorTileEntity> OXYGENATOR_TILE_ENTITY_TYPE = TileEntityType.Builder
            .create(OxygenatorTileEntity::new, MoonMod.oxygenatorBlock)
            .build(null);
    private final EnclosedSpaceAlgorithm enclosedSpaceAlgorithm;

    public OxygenatorTileEntity() {
        super(OXYGENATOR_TILE_ENTITY_TYPE);
        enclosedSpaceAlgorithm = new EnclosedSpaceAlgorithm();
    }

    @Override
    public void tick() {
        if (!MoonMod.MoonHelper.isMoon(world)) {
            return;
        }

        // See if this is an airtight room
        OxygenatedRegion region = enclosedSpaceAlgorithm.performCalculation(world, pos);
        if (region != null) {
            region.getOxygenStorage().accept(10);
        }
    }
}
