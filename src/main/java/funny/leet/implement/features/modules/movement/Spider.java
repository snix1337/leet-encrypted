package funny.leet.implement.features.modules.movement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import obf.uscate.annotations.Compile;
import funny.leet.api.event.EventHandler;
import funny.leet.api.event.types.EventType;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.SelectSetting;
import funny.leet.common.util.entity.PlayerInventoryUtil;
import funny.leet.common.util.entity.SimulatedPlayer;
import funny.leet.common.util.task.TaskPriority;
import funny.leet.common.util.task.scripts.Script;
import funny.leet.implement.events.player.RotationUpdateEvent;
import funny.leet.implement.features.modules.combat.killaura.rotation.Angle;
import funny.leet.implement.features.modules.combat.killaura.rotation.AngleUtil;
import funny.leet.implement.features.modules.combat.killaura.rotation.RotationConfig;
import funny.leet.implement.features.modules.combat.killaura.rotation.RotationController;
import funny.leet.implement.features.modules.combat.killaura.rotation.angle.SnapSmoothMode;

import java.util.stream.Stream;

@Setter
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Spider extends Module {
    Script script = new Script();

    SelectSetting mode = new SelectSetting("Mode", "Selects the type of spider")
            .value("Block").selected("Block");

    public Spider() {
        super("Spider", ModuleCategory.MOVEMENT);
        setup(mode);
    }

    @EventHandler
    public void onRotationUpdate(RotationUpdateEvent e) {
        if (mode.isSelected("Block") && e.getType() == EventType.PRE) {
            boolean offHand = mc.player.getOffHandStack().getItem() instanceof BlockItem;
            int slotId = PlayerInventoryUtil.getHotbarSlotId(i -> mc.player.getInventory().getStack(i).getItem() instanceof BlockItem);
            BlockPos blockPos = findPos();
            if (script.isFinished() && (offHand || slotId != -1) && !blockPos.equals(BlockPos.ORIGIN)) {
                ItemStack stack = offHand ? mc.player.getOffHandStack() : mc.player.getInventory().getStack(slotId);
                Hand hand = offHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
                Vec3d vec = blockPos.toCenterPos();
                Direction direction = Direction.getFacing(vec.x - mc.player.getX(), vec.y - mc.player.getY(), vec.z - mc.player.getZ());
                Angle angle = AngleUtil.calculateAngle(vec.subtract(new Vec3d(direction.getVector()).multiply(0.5)));
                Angle.VecRotation vecRotation = new Angle.VecRotation(angle, angle.toVector());
                RotationController.INSTANCE.rotateTo(vecRotation, mc.player, 1, new RotationConfig(new SnapSmoothMode(), true, true), TaskPriority.HIGH_IMPORTANCE_1, this);
                if (canPlace(stack)) {
                    int prev = mc.player.inventory.selectedSlot;
                    if (!offHand) mc.player.inventory.selectedSlot = slotId;
                    mc.interactionManager.interactBlock(mc.player, hand, new BlockHitResult(vec, direction.getOpposite(), blockPos, false));
                    mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(hand));
                    if (!offHand) mc.player.inventory.selectedSlot = prev;
                }
            }
        }
    }

    private boolean canPlace(ItemStack stack) {
        BlockPos blockPos = getBlockPos();
        if (blockPos.getY() >= mc.player.getBlockY()) return false;
        BlockItem blockItem = (BlockItem) stack.getItem();
        VoxelShape shape = blockItem.getBlock().getDefaultState().getCollisionShape(mc.world, blockPos);
        if (shape.isEmpty()) return false;
        Box box = shape.getBoundingBox().offset(blockPos);
        return !box.intersects(mc.player.getBoundingBox()) && box.intersects(SimulatedPlayer.simulateLocalPlayer(4).boundingBox);
    }

    @Compile
    private BlockPos findPos() {
        BlockPos blockPos = getBlockPos();
        if (mc.world.getBlockState(blockPos).isSolid()) return BlockPos.ORIGIN;
        return Stream.of(blockPos.west(), blockPos.east(), blockPos.south(), blockPos.north()).filter(pos -> mc.world.getBlockState(pos).isSolid()).findFirst().orElse(BlockPos.ORIGIN);
    }

    private BlockPos getBlockPos() {
        return BlockPos.ofFloored(SimulatedPlayer.simulateLocalPlayer(1).pos.add(0, -1e-3, 0));
    }
}
