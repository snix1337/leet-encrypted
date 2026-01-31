package funny.leet.implement.features.modules.movement;

import funny.leet.common.util.entity.MovingUtil;
import funny.leet.implement.events.player.InputEvent;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import obf.uscate.annotations.Compile;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.SelectSetting;
import funny.leet.common.util.math.StopWatch;
import funny.leet.common.util.entity.PlayerIntersectionUtil;
import funny.leet.common.util.entity.PlayerInventoryUtil;
import funny.leet.common.util.task.scripts.Script;
import funny.leet.implement.events.player.FireworkEvent;
import funny.leet.implement.events.player.TickEvent;
import funny.leet.implement.features.modules.combat.killaura.rotation.AngleUtil;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DragonFly extends Module {
    StopWatch stopWatch = new StopWatch(), swapWatch = new StopWatch();
    Script script = new Script();

    SelectSetting flyModeSetting = new SelectSetting("Flight Mode", "Selects the type of mode")
            .value("Swap");

    public DragonFly() {
        super("DragonFly", "Dragon Fly", ModuleCategory.MOVEMENT);
        setup(flyModeSetting);
    }

    @Compile
    @EventHandler
    public void onInput(InputEvent e) {
        if (mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Items.ELYTRA) && MovingUtil.hasPlayerMovement()) {
            if (mc.player.isOnGround()) e.setJumping(true);
            else if (!mc.player.isGliding()) PlayerIntersectionUtil.startFallFlying();
        }
    }

    @EventHandler
    public void onFireWork(FireworkEvent e) {
        stopWatch.reset();
    }

    @Compile
    @EventHandler
    public void onTick(TickEvent e) {
        if (flyModeSetting.isSelected("Swap")) {
            Slot elytra = PlayerInventoryUtil.getSlot(Items.ELYTRA);
            Slot fireWork = PlayerInventoryUtil.getSlot(Items.FIREWORK_ROCKET);
            if (!mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Items.ELYTRA) && elytra != null && fireWork != null && script.isFinished()) {
                if (stopWatch.finished(100)) {
                    int ticks = mc.player.isOnGround() ? 2 : 0;
                    if (ticks != 0) mc.player.jump();
                    script.cleanup().addTickStep(ticks, () -> {
                        PlayerInventoryUtil.moveItem(elytra, 6, false);
                        PlayerIntersectionUtil.startFallFlying();
                        PlayerInventoryUtil.swapAndUse(Items.FIREWORK_ROCKET, AngleUtil.cameraAngle(), false);
                        PlayerInventoryUtil.moveItem(elytra, 6, false);
                        PlayerInventoryUtil.closeScreen(true);
                        stopWatch.setMs(-500);
                    });
                }
            }
        }
        script.update();
    }
}
