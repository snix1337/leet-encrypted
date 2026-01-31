package funny.leet.implement.features.modules.combat;

import funny.leet.api.feature.module.setting.implement.GroupSetting;
import funny.leet.api.feature.module.setting.implement.MultiSelectSetting;
import funny.leet.api.feature.module.setting.implement.SelectSetting;
import funny.leet.api.feature.module.setting.implement.ValueSetting;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.implement.features.modules.combat.killaura.attack.AttackHandler;
import funny.leet.implement.features.modules.combat.killaura.attack.AttackPerpetrator;
import funny.leet.implement.features.modules.combat.killaura.rotation.*;
import funny.leet.implement.features.modules.combat.killaura.rotation.angle.*;
import funny.leet.implement.features.modules.combat.killaura.target.TargetSelector;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.*;
import funny.leet.api.event.EventHandler;
import funny.leet.api.event.types.EventType;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.system.animation.Animation;
import funny.leet.api.system.animation.Direction;
import funny.leet.api.system.animation.implement.DecelerateAnimation;
import funny.leet.common.util.other.Instance;
import funny.leet.common.util.render.Render3DUtil;
import funny.leet.common.util.task.TaskPriority;
import funny.leet.core.Main;
import funny.leet.implement.events.packet.PacketEvent;
import funny.leet.implement.events.player.RotationUpdateEvent;
import funny.leet.implement.events.render.WorldRenderEvent;
import funny.leet.implement.features.draggables.Notifications;
import funny.leet.implement.features.modules.render.Hud;
import funny.leet.common.util.entity.MovingUtil;

import java.util.Objects;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Aura extends Module {
    public static Aura getInstance() {
        return Instance.get(Aura.class);
    }

    Animation esp_anim = new DecelerateAnimation().setMs(400).setValue(1);
    TargetSelector targetSelector = new TargetSelector();
    PointFinder pointFinder = new PointFinder();
    @NonFinal
    LivingEntity target, lastTarget;
    Float maxDistance = 137.0f;

    ValueSetting attackRange = new ValueSetting("Attack Range", "Choose attack distance")
            .setValue(3.0F).range(1.0F, 6.0F);
    ValueSetting extendedRange = new ValueSetting("Extended Range", "Additional reach distance")
            .setValue(0.0F).range(0.0F, 3.0F);
    ValueSetting elytraRange = new ValueSetting("Elytra Range", "Attack distance when gliding with elytra")
            .setValue(18.0F).range(3.0F, 128.0F);
    ValueSetting predictionFactor = new ValueSetting("Prediction Factor", "Factor for predicting target movement")
            .setValue(0.3F).range(0.0F, 5.0F);

    MultiSelectSetting targetType = new MultiSelectSetting("Target Type", "Filters the entire list of targets by type")
            .value("Players", "Mobs", "Animals", "Projectiles", "Friends");

    MultiSelectSetting attackSetting = new MultiSelectSetting("Attack Setting", "Allows you to customize the attack")
            .value("Only Critical", "Dynamic Cooldown", "Break Shield", "UnPress Shield", "No Attack When Eat", "Ignore The Walls");

    SelectSetting correctionType = new SelectSetting("Correction Type", "Selects the type of correction")
            .value("Free", "Focused").selected("Free");

    GroupSetting correctionGroup = new GroupSetting("Move correction", "Prevents detection by movement sensitive anti-cheats")
            .settings(correctionType).setValue(true);

    SelectSetting aimMode = new SelectSetting("Rotation Type", "Allows you to select the rotation type")
            .value("FunTime", "Snap", "HvH").selected("Snap");

    SelectSetting targetEspType = new SelectSetting("Target Esp Type", "Selects the type of target esp")
            .value("Cube", "Circle", "Ghosts").selected("Circle");

    ValueSetting ghostSpeed = new ValueSetting("Ghost Speed", "Speed of ghost flying around the target")
            .setValue(1).range(1F, 2F).visible(() -> targetEspType.isSelected("Ghosts"));

    GroupSetting targetEspGroup = new GroupSetting("Target Esp", "Displays the player in the world")
            .settings(targetEspType, ghostSpeed).setValue(true);

    public Aura() {
        super("Aura", ModuleCategory.COMBAT);
        setup(attackRange, extendedRange, elytraRange, predictionFactor, attackSetting, aimMode, correctionGroup, targetType, targetEspGroup);
    }

    @Override
    public void deactivate() {
        targetSelector.releaseTarget();
        target = null;
        super.deactivate();
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent e) {
        esp_anim.setDirection(target != null ? Direction.FORWARDS : Direction.BACKWARDS);
        float anim = esp_anim.getOutput().floatValue();
        if (targetEspGroup.isValue() && lastTarget != null && !esp_anim.isFinished(Direction.BACKWARDS)) {
            float red = MathHelper.clamp((lastTarget.hurtTime - tickCounter.getTickDelta(false)) / 10, 0, 1);
            switch (targetEspType.getSelected()) {
                case "Cube" -> Render3DUtil.drawCube(lastTarget, anim, red);
                case "Circle" -> Render3DUtil.drawCircle(e.getStack(), lastTarget, anim, red);
                case "Ghosts" -> Render3DUtil.drawGhosts(lastTarget, anim, red, ghostSpeed.getValue());
            }
        }
    }

    @EventHandler
    public void onPacket(PacketEvent e) {
        if (e.getPacket() instanceof EntityStatusS2CPacket status && status.getStatus() == 30) {
            Entity entity = status.getEntity(mc.world);
            if (entity != null && entity.equals(target) && Hud.getInstance().notificationSettings.isSelected("Break Shield")) {
                Notifications.getInstance().addList(Text.literal("The player's shield was broken - ").append(entity.getDisplayName()), 3000);
            }
        }
    }

    @EventHandler
    public void onRotationUpdate(RotationUpdateEvent e) {
        switch (e.getType()) {
            case EventType.PRE -> {
                target = updateTarget();
                if (target != null) {
                    if (attackSetting.isSelected("Only Critical") && mc.player != null && mc.player.isOnGround() && MovingUtil.hasPlayerMovement()) {
                        mc.options.jumpKey.setPressed(true);
                    }
                    rotateToTarget(getConfig());
                    lastTarget = target;
                }
            }
            case EventType.POST -> {
                Render3DUtil.updateTargetEsp();
                if (target != null) Main.getInstance().getAttackPerpetrator().performAttack(getConfig());
            }
        }
    }

    private LivingEntity updateTarget() {
        TargetSelector.EntityFilter filter = new TargetSelector.EntityFilter(targetType.getSelected());
        float effectiveRange = mc.player.isGliding() ? elytraRange.getValue() : (attackRange.getValue() + extendedRange.getValue());
        targetSelector.searchTargets(mc.world.getEntities(), effectiveRange, 360, attackSetting.isSelected("Ignore The Walls"));
        targetSelector.validateTarget(filter::isValid);
        return targetSelector.getCurrentTarget();
    }

    private void rotateToTarget(AttackPerpetrator.AttackPerpetratorConfigurable config) {
        AttackHandler attackHandler = Main.getInstance().getAttackPerpetrator().getAttackHandler();
        RotationController controller = RotationController.INSTANCE;
        Angle.VecRotation rotation = new Angle.VecRotation(config.getAngle(), config.getAngle().toVector());
        RotationConfig rotationConfig = getRotationConfig();
        switch (aimMode.getSelected()) {
            case "FunTime" -> {
                if (attackHandler.canAttack(config, 3) ) {
                    controller.clear();
                    controller.rotateTo(rotation, target, 40, rotationConfig, TaskPriority.HIGH_IMPORTANCE_1, this);
                }
            }
            case "Snap" -> {
                if (attackHandler.canAttack(config, 1) || !attackHandler.getAttackTimer().finished(100)) {
                    controller.rotateTo(rotation, target, 1, rotationConfig, TaskPriority.HIGH_IMPORTANCE_1, this);
                }
            }
            case "HvH" -> controller.rotateTo(rotation, target, 1, rotationConfig, TaskPriority.HIGH_IMPORTANCE_1, this);
        }
    }

    public AttackPerpetrator.AttackPerpetratorConfigurable getConfig() {
        float effectiveRange = mc.player.isGliding() ? elytraRange.getValue() : (attackRange.getValue() + extendedRange.getValue());
        Vec3d targetPos = target.getPos().add(target.getVelocity().multiply(predictionFactor.getValue()));
        Box predictedBox = new Box(targetPos, targetPos.add(target.getWidth(), target.getHeight(), target.getWidth()));
        Vec3d eyePos = mc.player.getEyePos(); // For box render
        Pair<Vec3d, Box> point = pointFinder.computeVector(target, effectiveRange, RotationController.INSTANCE.getRotation(), getSmoothMode().randomValue(), attackSetting.isSelected("Ignore The Walls"));
        Angle angle = AngleUtil.fromVec3d(point.getLeft().subtract(eyePos));
        Box box = point.getRight();
        return new AttackPerpetrator.AttackPerpetratorConfigurable(target, angle, effectiveRange, attackSetting.getSelected(), aimMode, box);
    }

    public RotationConfig getRotationConfig() {
        return new RotationConfig(getSmoothMode(), correctionGroup.isValue(), correctionType.isSelected("Free"));
    }

    public AngleSmoothMode getSmoothMode() {
        return switch (aimMode.getSelected()) {
            case "FunTime" -> new FunTimeSmoothMode();
            case "Snap" -> new SnapSmoothMode();
            case "HvH" -> new HvhSmoothMode();
            default -> new LinearSmoothMode();
        };
    }
}
