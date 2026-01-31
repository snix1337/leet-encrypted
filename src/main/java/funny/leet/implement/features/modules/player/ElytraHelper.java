package funny.leet.implement.features.modules.player;

import funny.leet.api.feature.module.setting.implement.BooleanSetting;
import funny.leet.common.util.other.Instance;
import funny.leet.implement.features.modules.movement.AutoSprint;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.BindSetting;
import funny.leet.common.util.entity.PlayerIntersectionUtil;
import funny.leet.common.util.entity.PlayerInventoryUtil;
import funny.leet.common.util.task.scripts.Script;
import funny.leet.implement.events.keyboard.KeyEvent;
import funny.leet.implement.events.player.TickEvent;

import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ElytraHelper extends Module {
    public static ElytraHelper getInstance() {
        return Instance.get(ElytraHelper.class);
    }

    BindSetting elytraSetting = new BindSetting("Elytra Swap", "Changes place chest-plate with elytra");
    BindSetting fireworkSetting = new BindSetting("Use FireWork", "Swaps and uses fireworks");
    // BooleanSetting canUseFirework = new BooleanSetting("FW On Broken Elytra", "Allow to use firework on broken elytra").setValue(true);
    BooleanSetting startSetting = new BooleanSetting("Quick Start", "When swapping to elytra, it takes off and uses fireworks").setValue(false);
    BooleanSetting wchSetting = new BooleanSetting("Use Wind Charge", "When swapping to elytra, it uses wind charge and takes off").setValue(false);
    Script script = new Script();

    public ElytraHelper() {
        super("ElytraHelper", "Elytra Helper", ModuleCategory.PLAYER);
        setup(elytraSetting, fireworkSetting, startSetting, wchSetting);
    }


    @EventHandler
    public void onKey(KeyEvent e) {
        if (!script.isFinished()) return;

        if (e.isKeyDown(elytraSetting.getKey())) {
            Slot slot = chestPlate();
            if (slot != null) {
                Slot fireWork = PlayerInventoryUtil.getSlot(Items.FIREWORK_ROCKET);
                Slot windCharge = PlayerInventoryUtil.getSlot(Items.WIND_CHARGE);
                boolean elytra = slot.getStack().getItem().equals(Items.ELYTRA);
                PlayerInventoryUtil.moveItem(slot, 6, true, true);
                    if (startSetting.isValue() && fireWork != null && elytra) script.cleanup().addTickStep(4, () -> {
                    if (mc.player.isOnGround()) mc.player.jump();
                }).addTickStep(3, () -> {
                    PlayerIntersectionUtil.startFallFlying();
                    PlayerInventoryUtil.swapAndUse(Items.FIREWORK_ROCKET);
                });
                // селфхарм ой бл9дь самопальная система хелпа при обнаружении заряда ветра (макс.ебебо левел)
                    PlayerInventoryUtil.moveItem(slot, 6, true, true);
                    if (wchSetting.isValue() && windCharge != null && elytra) script.cleanup().addTickStep(4, () -> {if (mc.player.isOnGround()) mc.player.jump();
                    if (mc.player.isOnGround()) mc.player.jump();
                    // залупа понеславь, векторно хуярим прицел по градусам до упора
                        // делал шникс под мефом***
                    if (mc.player.isOnGround()) mc.player.changeLookDirection(0,900);
                        // импрув сейв***
                    if (mc.player.isGliding()) mc.player.changeLookDirection(0,0);
                }).addTickStep(3, () -> {
                    // дефолт клиент утилки
                    PlayerIntersectionUtil.startFallFlying();
                    PlayerInventoryUtil.swapAndUse(Items.WIND_CHARGE);
                });
            }
        } else if (e.isKeyDown(fireworkSetting.getKey()) && mc.player.isGliding()) {
            PlayerInventoryUtil.swapAndUse(Items.FIREWORK_ROCKET);
        }
    }

    @EventHandler
    public void onTick(TickEvent e) {
        script.update();
    }


    private Slot chestPlate() {
        if (Objects.requireNonNull(mc.player).getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Items.ELYTRA))
            return PlayerInventoryUtil.getSlot(List.of(Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.CHAINMAIL_CHESTPLATE, Items.IRON_CHESTPLATE, Items.GOLDEN_CHESTPLATE, Items.LEATHER_CHESTPLATE));
        else return PlayerInventoryUtil.getSlot(Items.ELYTRA);
    }
}
