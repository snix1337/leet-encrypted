package funny.leet.implement.features.modules.movement;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.math.Vec3d;
import obf.uscate.annotations.Compile;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.ValueSetting;
import funny.leet.common.util.entity.MovingUtil;
import funny.leet.implement.events.player.MoveEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Flight extends Module {

    ValueSetting speedSetting = new ValueSetting("Speed", "Flight speed")
            .setValue(2.0F)
            .range(0.1F, 5.0F);

    public Flight() {
        super("Flight", "Flight", ModuleCategory.MOVEMENT);
        setup(speedSetting);
    }

    @Compile
    @EventHandler
    public void onMove(MoveEvent e) {
        if (mc.player == null || mc.world == null) return;

        // Отключаем гравитацию — чтобы не падать
        mc.player.setVelocity(mc.player.getVelocity().x, 0, mc.player.getVelocity().z);
        mc.player.fallDistance = 0.0f;

        float speed = speedSetting.getValue();

        // Вертикальное движение: Shift = вниз, Space = вверх
        double y = 0.0;
        if (mc.options.sneakKey.isPressed()) {
            y = -speed;
        } else if (mc.options.jumpKey.isPressed()) {
            y = speed;
        }

        // Горизонтальное движение
        double[] motion = MovingUtil.calculateDirection(speed);
        double x = motion[0];
        double z = motion[1];

        // Если не двигаемся — стоим на месте (не плывём по инерции)
        if (!mc.options.forwardKey.isPressed() &&
                !mc.options.backKey.isPressed() &&
                !mc.options.leftKey.isPressed() &&
                !mc.options.rightKey.isPressed()) {
            x = 0;
            z = 0;
        }

        // Устанавливаем движение
        e.setMovement(new Vec3d(x, y, z));
    }

    public void onDisable() {
        if (mc.player != null) {
            // Восстанавливаем гравитацию при выключении
            mc.player.fallDistance = 0.0f;
        }
    }
}
