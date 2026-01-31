package funny.leet.implement.features.modules.movement;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.ShulkerBoxScreenHandler;
import net.minecraft.util.math.BlockPos;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.common.util.entity.PlayerInventoryUtil;
import funny.leet.implement.events.player.TickEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class HightJump extends Module {
    int tick = 0;
    boolean start = false;

    public HightJump() {
        super("HightJump", "Hight Jump", ModuleCategory.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent e) {
        BlockPos blockX = new BlockPos(mc.player.getBlockX()+1, mc.player.getBlockY(), mc.player.getBlockZ());
        BlockPos blockNegativeX = new BlockPos(mc.player.getBlockX()-1, mc.player.getBlockY(), mc.player.getBlockZ());
        BlockPos blockZ = new BlockPos(mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ()+1);
        BlockPos blockNegativeZ = new BlockPos(mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ()-1);

        if (PlayerInventoryUtil.getBlockState(blockX) instanceof ShulkerBoxBlock || PlayerInventoryUtil.getBlockState(blockZ) instanceof ShulkerBoxBlock || PlayerInventoryUtil.getBlockState(blockNegativeX) instanceof ShulkerBoxBlock || PlayerInventoryUtil.getBlockState(blockNegativeZ) instanceof ShulkerBoxBlock) {
            if (mc.player.currentScreenHandler instanceof ShulkerBoxScreenHandler) {
                mc.options.jumpKey.setPressed(true);
                mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(mc.player.currentScreenHandler.syncId));
                mc.player.addVelocity(0, 2.40, 0);
                start = true;
            }
        }
        if (mc.player.currentScreenHandler instanceof ShulkerBoxScreenHandler && start) {
            if (tick >= 5) {
                mc.player.closeHandledScreen();
                start = false;
                tick = 0;
            } else tick++;

        }
    }
}
