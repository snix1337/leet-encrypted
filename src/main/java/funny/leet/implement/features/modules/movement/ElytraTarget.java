package funny.leet.implement.features.modules.movement;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import obf.uscate.annotations.Compile;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.implement.events.player.TickEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ElytraTarget extends Module {
    int timer = 0;
    int originalSlot = -1;
    static int sequenceCounter = 0;

    public ElytraTarget() {
        super("ElytraTarget", "Elytra Target", ModuleCategory.MOVEMENT);
    }

    @Compile
    @EventHandler
    public void onTick(TickEvent e) {
        if (mc.player == null || mc.world == null || !mc.player.isGliding()) {
            timer = 0;
            originalSlot = -1;
            return;
        }

        Entity target = mc.targetedEntity;
        if (target == null) {
            timer = 0;
            originalSlot = -1;
            return;
        }

        lookAtTarget(target);

    }

    private void lookAtTarget(Entity target) {
        if (mc.player == null || target == null) return;

        Vec3d targetPos = target.getPos().add(0, target.getHeight() / 2, 0);
        Vec3d playerPos = mc.player.getPos().add(0, mc.player.getEyeHeight(mc.player.getPose()), 0);
        Vec3d direction = targetPos.subtract(playerPos);

        float yaw = (float) (Math.atan2(direction.z, direction.x) * 180 / Math.PI) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

        mc.player.setYaw(MathHelper.wrapDegrees(yaw));
        mc.player.setPitch(MathHelper.clamp(pitch, -90, 90));
    }

    public void onDisable() {
        timer = 0;
        originalSlot = -1;
        sequenceCounter = 0;
    }
}
