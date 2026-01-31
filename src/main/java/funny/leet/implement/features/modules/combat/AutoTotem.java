package funny.leet.implement.features.modules.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Hand;
import obf.uscate.annotations.Compile;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.MultiSelectSetting;
import funny.leet.api.feature.module.setting.implement.ValueSetting;
import funny.leet.common.util.entity.PlayerIntersectionUtil;
import funny.leet.common.util.math.StopWatch;
import funny.leet.common.util.entity.PlayerInventoryUtil;
import funny.leet.common.util.task.scripts.Script;
import funny.leet.implement.events.player.TickEvent;

import java.util.Comparator;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AutoTotem extends Module {

    ValueSetting healthSetting = new ValueSetting("Max Health", "Maximum health for triggering Auto Totem")
            .setValue(6F).range(2F, 48F);

    MultiSelectSetting triggerSetting = new MultiSelectSetting("Triggers", "Select in which case the Totem will be taken")
            .value("Elytra Fall", "Crystal", "TNT");

    ValueSetting fallDistanceSetting = new ValueSetting("Fall Distance", "Minimum fall distance to trigger on elytra")
            .setValue(10F).range(3F, 128F).visible(() -> triggerSetting.isSelected("Elytra Fall"));

    ValueSetting TNTRangeSetting = new ValueSetting("TNT Distance", "Distance to TNT for triggering Auto Totem")
            .setValue(8F).range(0F, 50F).visible(() -> triggerSetting.isSelected("TNT"));

    StopWatch stopWatch = new StopWatch();
    Script script = new Script();

    public AutoTotem() {
        super("AutoTotem", "Auto Totem", ModuleCategory.COMBAT);
        setup(healthSetting, triggerSetting, fallDistanceSetting, TNTRangeSetting);
    }

    @Compile
    @Override
    public void deactivate() {
        script.update();
        super.deactivate();
    }

    @EventHandler
    public void onTick(TickEvent e) {
        if (trigger()) {
            ItemStack offHandStack = mc.player.getOffHandStack();
            Slot slot = PlayerInventoryUtil.getSlot(Items.TOTEM_OF_UNDYING,
                    Comparator.comparing(s -> !s.getStack().hasEnchantments()),
                    s -> s.id != 46 && s.id != 45);

            if (slot == null) return;

            boolean needSwap = !offHandStack.getItem().equals(Items.TOTEM_OF_UNDYING) ||
                    (offHandStack.hasEnchantments() && !slot.getStack().hasEnchantments());

            if (needSwap && stopWatch.every(0)) {
                PlayerInventoryUtil.swapHand(slot, Hand.OFF_HAND, true, true);
                if (script.isFinished()) {
                    script.cleanup().addTickStep(0, () ->
                            PlayerInventoryUtil.swapHand(slot, Hand.OFF_HAND, true, true));
                }
            }
        } else if (!script.isFinished() && stopWatch.every(0)) {
            script.update();
        }
    }

    public boolean trigger() {
        float health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        boolean elytraEquipped = mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Items.ELYTRA);
        float elytraBonus = elytraEquipped ? 4 : 0;

        if (mc.player.getItemCooldownManager().isCoolingDown(Items.TOTEM_OF_UNDYING.getDefaultStack())) return false;
        if (health < healthSetting.getValue() + elytraBonus) return true;

        if (triggerSetting.isSelected("Elytra Fall") && elytraEquipped && mc.player.fallDistance >= fallDistanceSetting.getValue())
            return true;

        if (triggerSetting.isSelected("Crystal") &&
                PlayerIntersectionUtil.streamEntities().anyMatch(e ->
                        e instanceof EndCrystalEntity && mc.player.distanceTo(e) < 5 && e.getY() > mc.player.getEyeY()))
            return true;

        if (triggerSetting.isSelected("TNT") &&
                PlayerIntersectionUtil.streamEntities().anyMatch(e ->
                        e instanceof TntEntity && mc.player.distanceTo(e) < TNTRangeSetting.getValue()))
            return true;

        return false;
    }
}
