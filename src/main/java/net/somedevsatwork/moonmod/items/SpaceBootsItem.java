package net.somedevsatwork.moonmod.items;

import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.somedevsatwork.moonmod.MoonMod;

import javax.annotation.Nullable;

public class SpaceBootsItem extends ArmorItem {
    private final static Properties builder = new Item.Properties().maxStackSize(1).group(MoonMod.moonModItemGroup);

    public SpaceBootsItem() {
        super(new SpaceArmorMaterial(), EquipmentSlotType.FEET, builder);
    }

    @Nullable
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        return MoonMod.MODID + ":textures/armor/space_layer_1.png";
    }
}
