package net.somedevsatwork.moonmod.capability;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.HashSet;

public class OxygenatedRegion {
    /**
     * The amount of oxygen that can be held in one block.
     * This is used with the size of the region in order to determine
     * the capacity of this region's {@link IOxygenStorage}.
     */
    private static final int OXYGEN_CAPACITY_PER_BLOCK = 250;

    private final HashSet<BlockPos> regionPositions;
    private final IOxygenStorage oxygenStorage;

    public OxygenatedRegion(HashSet<BlockPos> givenRegion) {
        regionPositions = givenRegion;
        oxygenStorage = new OxygenStorage(regionPositions.size() * OXYGEN_CAPACITY_PER_BLOCK, 0);
    }

    @Nonnull
    public IOxygenStorage getOxygenStorage() {
        return oxygenStorage;
    }

    public boolean positionInRegion(BlockPos pos) {
        return regionPositions.contains(pos);
    }
}
