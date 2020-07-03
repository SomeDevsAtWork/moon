package net.somedevsatwork.moonmod.algorithms;

import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.somedevsatwork.moonmod.MoonMod;
import net.somedevsatwork.moonmod.capability.OxygenatedRegion;

import javax.annotation.Nullable;
import java.util.HashSet;

public class EnclosedSpaceAlgorithm {
    private static final int MAX_ITERATIONS = 15;

    private final HashSet<BlockPos> positions;
    private BlockPos initialPosition;

    public EnclosedSpaceAlgorithm() {
        positions = new HashSet<>();
    }

    @Nullable
    public OxygenatedRegion performCalculation(World world, BlockPos initialPosition) {
        positions.clear();
        this.initialPosition = initialPosition;

        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            if (!step(world)) {
                continue;
            }

            // Determined to be in an enclosed space.
            // Return an already existing region from these positions or create a new one.
            // TODO - check if this region should be merged with another (could break one block and place one behind it,
            // TODO - still cutting this off as an enclosed space but now having a different hashcode because there is
            // TODO - 1 more AIR block that is oxygenated.
            int positionsHashCode = positions.hashCode();
            if (MoonMod.oxygenRegions.containsKey(positionsHashCode)) {
                return MoonMod.oxygenRegions.get(positionsHashCode);
            } else {
                OxygenatedRegion region = new OxygenatedRegion(positions);
                MoonMod.oxygenRegions.put(positionsHashCode, region);
                return region;
            }
        }

        return null;
    }

    private boolean isAir(World world, BlockPos pos) {
        return world.getBlockState(pos).getBlock() == Blocks.AIR;
    }

    private boolean step(World world) {
        HashSet<BlockPos> nextStep = new HashSet<>();

        if (positions.size() == 0) {
            // Use initial position
            for (Direction d : Direction.values()) {
                BlockPos newPos = initialPosition.offset(d);
                if (isAir(world, newPos)) {
                    nextStep.add(initialPosition.offset(d));
                }
            }
        } else {
            positions.forEach(position -> {
                for (Direction d : Direction.values()) {
                    BlockPos newPos = position.offset(d);
                    if (isAir(world, newPos)) {
                        nextStep.add(position.offset(d));
                    }
                }
            });
        }

        boolean finalStep = positions.equals(nextStep);
        positions.addAll(nextStep);
        return finalStep;
    }
}
