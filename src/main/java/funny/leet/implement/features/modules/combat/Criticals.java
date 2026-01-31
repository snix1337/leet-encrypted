package funny.leet.implement.features.modules.combat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import obf.uscate.annotations.Compile;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.SelectSetting;
import funny.leet.common.util.math.MathUtil;
import funny.leet.common.util.entity.PlayerIntersectionUtil;
import funny.leet.common.util.other.Instance;
import funny.leet.implement.events.player.AttackEvent;
import funny.leet.implement.features.modules.combat.killaura.rotation.RotationController;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Criticals extends Module {
    public static Criticals getInstance() {
        return Instance.get(Criticals.class);
    }

    SelectSetting mode = new SelectSetting("Mode", "Select bypass mode").value("Grim", "HvH");

    public Criticals() {
        super("Criticals", ModuleCategory.COMBAT);
        setup(mode);
    }

    @Compile
    @EventHandler
    public void onAttack(AttackEvent e) {
        if (mc.player.isTouchingWater()) return;
        if (mode.isSelected("Grim")) {
            if (!mc.player.isOnGround() && mc.player.fallDistance == 0) {
                PlayerIntersectionUtil.grimCritBypass(-(mc.player.fallDistance = MathUtil.getRandom(1e-5F, 1e-4F)), RotationController.INSTANCE.getRotation().random(1e-3F));
            }
        } else if (mode.isSelected("HvH")) {
            if (!mc.player.isOnGround() && mc.player.fallDistance == 0) {
                double fallDelta = MathUtil.getRandom(0.001, 0.005);
                mc.player.fallDistance = (float) fallDelta;
                PlayerIntersectionUtil.hvhCritBypass(-(fallDelta), RotationController.INSTANCE.getRotation().random(0.001F));
            }
        }
    }
}
