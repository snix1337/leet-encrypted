package funny.leet.implement.features.modules.render;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.other.Instance;
import funny.leet.common.util.render.Render3DUtil;
import funny.leet.implement.events.render.WorldRenderEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class BlockHighLight extends Module {
    public static BlockHighLight getInstance() {
        return Instance.get(BlockHighLight.class);
    }

    public BlockHighLight() {
        super("BlockHighLight", "Block HighLight", ModuleCategory.RENDER);
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent e) {
        if (mc.crosshairTarget instanceof BlockHitResult result && result.getType().equals(HitResult.Type.BLOCK)) {
            BlockPos pos = result.getBlockPos();
            Render3DUtil.drawShapeAlternative(pos, mc.world.getBlockState(pos).getOutlineShape(mc.world, pos), ColorUtil.getClientColor(), 2, true, true);
        }
    }
}
