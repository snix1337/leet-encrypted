package funny.leet.implement.features.modules.movement;

import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.common.util.math.TickTimer;
import funny.leet.common.util.other.Instance;
import funny.leet.common.util.world.ServerUtil;
import funny.leet.implement.events.player.MoveEvent;
import funny.leet.implement.events.player.TickEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class AirStuck extends Module {
    public AirStuck() {
        super("AirStuck", "Air Stuck", ModuleCategory.MOVEMENT);
    }

    public Vec3d pos, prevPos;
    private World lastWorld;

    final TickTimer freezeTimer = new TickTimer();
    boolean prevReallyWorld = false;
    boolean prevGliding = false;

    public static AirStuck getInstance() {
        return Instance.get(AirStuck.class);
    }

    @Override
    public void activate() {
        prevPos = pos = new Vec3d(mc.getEntityRenderDispatcher().camera.getPos().toVector3f());
        lastWorld = mc.player.getWorld();
        super.activate();
    }

    @Override
    public void deactivate() {
        lastWorld = null;
        super.deactivate();
    }

    @EventHandler
    public void onMove(MoveEvent e) {
        boolean isReallyWorld = ServerUtil.isReallyWorld();

        if (!isReallyWorld) {
            e.setMovement(Vec3d.ZERO);
            return;
        }

        boolean isGliding = mc.player.isGliding();
        int freezeTicks = isGliding ? 250 : 10;

        if (freezeTimer.ticks < freezeTicks) {
            e.setMovement(Vec3d.ZERO);
        }
    }

    @EventHandler
    public void onUpdate(TickEvent event) {
        if (mc.world == null || mc.player == null || mc.player.getWorld() != lastWorld) {
            this.setState(false);
            return;
        }

        boolean currReallyWorld = ServerUtil.isReallyWorld() || ServerUtil.isFunTime();
        boolean currGliding = mc.player.isGliding();

        if (currReallyWorld != prevReallyWorld || currGliding != prevGliding) {
            freezeTimer.reset();
            prevReallyWorld = currReallyWorld;
            prevGliding = currGliding;
        }

        if (currReallyWorld) {
            freezeTimer.ticks++;
            int freezeTicks = currGliding ? 250 : 10;
            int resetTicks = currGliding ? 40 : 10;
            if (freezeTimer.ticks >= freezeTicks + resetTicks) {
                freezeTimer.reset();
            }
        }
    }
}
