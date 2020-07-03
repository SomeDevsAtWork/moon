package net.somedevsatwork.moonmod.capability;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.somedevsatwork.moonmod.MoonMod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityOxygen {
    @CapabilityInject(IOxygenStorage.class)
    public static Capability<IOxygenStorage> OXYGEN = null;

    public static final ResourceLocation PLAYER_RESOURCE = new ResourceLocation(MoonMod.MODID, "player_oxygen_cap");

    public static void register() {
        CapabilityManager.INSTANCE.register(IOxygenStorage.class, new Capability.IStorage<IOxygenStorage>() {
            @Override
            public INBT writeNBT(Capability<IOxygenStorage> capability, IOxygenStorage instance, Direction side)
            {
                return IntNBT.valueOf(instance.getLevel());
            }

            @Override
            public void readNBT(Capability<IOxygenStorage> capability, IOxygenStorage instance, Direction side, INBT nbt)
            {
                if (!(instance instanceof OxygenStorage))
                    throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
                ((OxygenStorage)instance).oxygenLevel = ((IntNBT)nbt).getInt();
            }
        }, () -> new OxygenStorage(100, 0));
    }

    public static class Provider implements ICapabilityProvider {
        private final LazyOptional<IOxygenStorage> instance = LazyOptional.of(OXYGEN::getDefaultInstance);

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return cap == OXYGEN ? instance.cast() : LazyOptional.empty();
        }
    }
}



