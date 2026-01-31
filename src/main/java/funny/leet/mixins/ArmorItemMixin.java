package funny.leet.mixins;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterial;
import net.minecraft.item.equipment.EquipmentType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import funny.leet.interfaces.IArmorItem;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin implements IArmorItem {

    @Unique
    private ArmorMaterial armorMaterial;

    @Unique
    private EquipmentType type;

    @Inject(method = "<init>", at = @At("RETURN"))
    public void hookCatchArgs(ArmorMaterial material, EquipmentType type, Item.Settings settings, CallbackInfo ci) {
        this.armorMaterial = material;
        this.type = type;
    }

    @Override
    public ArmorMaterial armor$getMaterial() {
        return armorMaterial;
    }

    @Override
    public EquipmentType armor$getType() {
        return type;
    }
}
