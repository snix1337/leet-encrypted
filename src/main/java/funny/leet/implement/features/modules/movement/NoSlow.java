package funny.leet.implement.features.modules.movement;

import funny.leet.common.util.entity.MovingUtil;
import funny.leet.common.util.entity.PlayerIntersectionUtil;
import funny.leet.common.util.entity.PlayerInventoryComponent;
import funny.leet.common.util.entity.PlayerInventoryUtil;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Blocks;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Hand;
import funny.leet.api.event.EventHandler;
import funny.leet.api.event.types.EventType;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.MultiSelectSetting;
import funny.leet.api.feature.module.setting.implement.SelectSetting;
import funny.leet.common.util.other.Instance;
import funny.leet.common.util.math.StopWatch;
import funny.leet.common.util.task.scripts.Script;
import funny.leet.implement.events.item.UsingItemEvent;
import funny.leet.implement.events.player.TickEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class NoSlow extends Module {
    public static NoSlow getInstance() {
        return Instance.get(NoSlow.class);
    }

    private final StopWatch notifWatch = new StopWatch();
    private final Script script = new Script();
    private boolean finish;

    public final MultiSelectSetting slowTypeSetting = new MultiSelectSetting("Target Type", "Filters the entire list of targets by type").value("Using Item", "Web", "Sneaking");
    public final SelectSetting itemMode = new SelectSetting("Item Mode", "Select bypass mode").value("Grim New", "Grim Old", "Eat NoSlow").visible(() -> slowTypeSetting.isSelected("Using Item"));
    public final SelectSetting webMode = new SelectSetting("Web Mode", "Select bypass mode").value("Grim").visible(() -> slowTypeSetting.isSelected("Web"));
    public final SelectSetting sneakMode = new SelectSetting("Sneak Mode", "Select bypass mode").value("Grim").visible(() -> slowTypeSetting.isSelected("Sneaking"));

    public NoSlow() {
        super("NoSlow", "No Slow", ModuleCategory.MOVEMENT);
        setup(slowTypeSetting, itemMode, webMode, sneakMode);
    }

    @EventHandler
    public void onTick(TickEvent e) {
        if (slowTypeSetting.isSelected("Web") && PlayerIntersectionUtil.isPlayerInBlock(Blocks.COBWEB)) {
            double[] speed = MovingUtil.calculateDirection(0.64);
            mc.player.addVelocity(speed[0], 0, speed[1]);
            mc.player.velocity.y = mc.options.jumpKey.isPressed() ? 1.2 : mc.options.sneakKey.isPressed() ? -2 : 0;
        }
        if (slowTypeSetting.isSelected("Sneaking") && mc.options.sneakKey.isPressed() && !mc.player.isOnGround()) {
            double baseSpeed = 0.2873;
            double boostFactor = 1.3;
            double[] speed = MovingUtil.calculateDirection(baseSpeed * boostFactor);
            mc.player.addVelocity(speed[0], 0, speed[1]);
        }
        if (PlayerInventoryComponent.script.isFinished() && MovingUtil.hasPlayerMovement()) {
            script.update();
        }
    }

    @EventHandler
    public void onUsingItem(UsingItemEvent e) {
        if (slowTypeSetting.isSelected("Using Item")) {
            Hand first = mc.player.getActiveHand();
            Hand second = first.equals(Hand.MAIN_HAND) ? Hand.OFF_HAND : Hand.MAIN_HAND;
            switch (e.getType()) {
                case EventType.ON -> {
                    switch (itemMode.getSelected()) {
                        case "Grim Old" -> {
                            if (mc.player.getOffHandStack().getUseAction().equals(UseAction.NONE) || mc.player.getMainHandStack().getUseAction().equals(UseAction.NONE)) {
                                PlayerIntersectionUtil.interactItem(first);
                                PlayerIntersectionUtil.interactItem(second);
                                e.cancel();
                            }
                        }
                        case "Grim New" -> {
                            if (mc.player.getItemUseTime() < 7) {
                                PlayerInventoryUtil.updateSlots();
                                PlayerInventoryUtil.closeScreen(true);
                            } else e.cancel();
                        }
                        case "Eat NoSlow" -> {
                            if (mc.player.getActiveItem().getUseAction() == UseAction.EAT || mc.player.getActiveItem().getUseAction() == UseAction.DRINK) {
                                if (mc.player.getItemUseTime() % 4 == 0) {
                                    PlayerIntersectionUtil.interactItem(first);
                                    e.cancel();
                                }
                                double baseSpeed = 0.2873;
                                double[] speed = MovingUtil.calculateDirection(baseSpeed);
                                mc.player.addVelocity(speed[0] * 0.1, 0, speed[1] * 0.1);
                            }
                        }
                        case "FunTime" -> {
                            if (finish) e.cancel();
                        }
                    }
                }
                case EventType.POST -> {
                    while (!script.isFinished()) script.update();
                }
            }
        }
    }
}
