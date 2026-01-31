package funny.leet.implement.features.modules.movement;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import funny.leet.api.event.EventHandler;
import funny.leet.implement.events.player.SwimmingEvent;
import funny.leet.implement.events.player.TickEvent;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.implement.features.modules.combat.killaura.rotation.RotationController;
import funny.leet.api.feature.module.setting.implement.SelectSetting;
import funny.leet.common.util.entity.MovingUtil;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WaterSpeed extends Module {

    private static final double WALL_BOOST = 0.08;
    private static final double WALL_VERTICAL = 0.05;
    private static final double WALL_RADIUS = 0.5;

    SelectSetting modeSetting = new SelectSetting("Mode", "Select bypass mode").value("FunTime", "FTWall");

    public WaterSpeed() {
        super("WaterSpeed", "Water Speed", ModuleCategory.MOVEMENT);
        setup(modeSetting);
    }

    @EventHandler
    public void onTick(TickEvent e) {
        if (mc.player == null || mc.world == null) return;

        switch (modeSetting.getSelected()) {
            case "FunTime" -> handleFunTimeTick();
            case "FTWall" -> handleFTWall();
        }
    }

    private void handleFunTimeTick() {
        if (mc.player.isSwimming() && mc.player.isOnGround()) {
            mc.player.jump();
            mc.player.velocity.y = 0.1;
        }
    }

    private void handleFTWall() {
        if (!mc.player.isTouchingWater()) return;
        if (!mc.player.horizontalCollision) return;
        if (!isWaterNearFeet()) return;

        Direction face = mc.player.getHorizontalFacing();
        Direction collisionFace = getCollisionFace();
        if (collisionFace != null) {
            face = collisionFace;
        }

        Vec3d pushDir = new Vec3d(-face.getOffsetX(), 0.0, -face.getOffsetZ());
        if (pushDir.lengthSquared() < 1e-6) {
            return;
        }

        double[] moveDir = MovingUtil.calculateDirection(mc.player.input.movementForward, mc.player.input.movementSideways, WALL_BOOST);
        Vec3d combined = new Vec3d(moveDir[0], 0.0, moveDir[1])
                .add(pushDir.normalize().multiply(WALL_BOOST * 0.6));

        Vec3d velocity = mc.player.getVelocity();
        Vec3d result = velocity.add(combined);

        double vertical = Math.max(velocity.y, WALL_VERTICAL);

        mc.player.setVelocity(result.x, vertical, result.z);
        mc.player.fallDistance = 0;
    }

    private Direction getCollisionFace() {
        Box box = mc.player.getBoundingBox();
        for (Direction dir : Direction.Type.HORIZONTAL) {
            Box shifted = box.offset(dir.getOffsetX() * 0.05, 0, dir.getOffsetZ() * 0.05);
            if (!mc.world.isSpaceEmpty(mc.player, shifted)) {
                return dir;
            }
        }
        return null;
    }

    private boolean isWaterNearFeet() {
        Box box = mc.player.getBoundingBox();
        int minX = MathHelper.floor(box.minX - WALL_RADIUS);
        int maxX = MathHelper.floor(box.maxX + WALL_RADIUS);
        int minY = MathHelper.floor(box.minY - 0.2);
        int maxY = MathHelper.floor(box.minY + 0.2);
        int minZ = MathHelper.floor(box.minZ - WALL_RADIUS);
        int maxZ = MathHelper.floor(box.maxZ + WALL_RADIUS);

        for (BlockPos pos : BlockPos.iterate(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (mc.world.getFluidState(pos).isIn(FluidTags.WATER)) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onSwimming(SwimmingEvent e) {
        if (modeSetting.isSelected("FunTime")) {
            if (mc.options.jumpKey.isPressed()) {
                float pitch = RotationController.INSTANCE.getRotation().getPitch();
                float boost = pitch >= 0 ? MathHelper.clamp(pitch / 45, 1, 2) : 1;
                e.getVector().y = 1 * boost;
            } else if (mc.options.sneakKey.isPressed()) {
                e.getVector().y = -0.8;
            }
        }
    }
}
