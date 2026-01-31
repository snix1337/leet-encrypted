package funny.leet.implement.features.modules.movement;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Items;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.BooleanSetting;
import funny.leet.common.util.entity.MovingUtil;
import funny.leet.common.util.entity.PlayerIntersectionUtil;
import funny.leet.implement.events.player.InputEvent;
import obf.uscate.annotations.Compile;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ElytraRecast extends Module {

    private final BooleanSetting requireJumpKey = new BooleanSetting("Require Space", "Only recast while holding jump key")
            .setValue(false);

    public ElytraRecast() {
        super("ElytraRecast", "Elytra Recast", ModuleCategory.MOVEMENT);
        setup(requireJumpKey);
    }

    @Compile
    @EventHandler
    public void onInput(InputEvent e) {
        if (!mc.player.getEquippedStack(EquipmentSlot.CHEST).getItem().equals(Items.ELYTRA) || !MovingUtil.hasPlayerMovement()) {
            return;
        }

        boolean jumpHeld = mc.options.jumpKey.isPressed();

        if (requireJumpKey.isValue() && !jumpHeld) {
            return;
        }

        if (mc.player.isOnGround()) {
            e.setJumping(true);
        } else if (!mc.player.isGliding()) {
            PlayerIntersectionUtil.startFallFlying();
        }
    }
}
