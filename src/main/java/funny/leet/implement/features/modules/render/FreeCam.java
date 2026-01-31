package funny.leet.implement.features.modules.render;

import funny.leet.common.util.color.ColorUtil;
import funny.leet.implement.events.player.InputEvent;
import funny.leet.implement.events.player.MoveEvent;
import funny.leet.implement.events.player.TickEvent;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.client.option.Perspective;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.BooleanSetting;
import funny.leet.api.feature.module.setting.implement.ValueSetting;
import funny.leet.common.util.math.MathUtil;
import funny.leet.common.util.entity.MovingUtil;
import funny.leet.common.util.other.Instance;
import funny.leet.common.util.render.Render3DUtil;
import funny.leet.implement.events.packet.PacketEvent;
import funny.leet.implement.events.render.CameraPositionEvent;
import funny.leet.implement.events.render.WorldRenderEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class FreeCam extends Module {
    public static FreeCam getInstance() {
        return Instance.get(FreeCam.class);
    }

    Box box;
    public Vec3d pos;
    Vec3d prevPos;
    Vec3d frozenPos;

    final ValueSetting speedSetting = new ValueSetting("Speed", "Select debug camera speed").setValue(1.0F).range(0.5F, 5.0F);
    final BooleanSetting freezeSetting = new BooleanSetting("Stuck", "Stuck player position while module is active").setValue(false);

    public FreeCam() {
        super("FreeCam", "Free Cam", ModuleCategory.RENDER);
        setup(freezeSetting, speedSetting);
    }

    @Override
    public void activate() {
        prevPos = pos = new Vec3d(mc.getEntityRenderDispatcher().camera.getPos().toVector3f());
        box = mc.player.getBoundingBox();
        frozenPos = mc.player.getPos();
        super.activate();
    }

    @EventHandler
    public void onPacket(PacketEvent e) {
        switch (e.getPacket()) {
            case PlayerMoveC2SPacket move when freezeSetting.isValue() -> e.cancel();
            case PlayerRespawnS2CPacket respawn -> {
                frozenPos = mc.player.getPos();
                setState(false);
            }
            case GameJoinS2CPacket join -> {
                frozenPos = mc.player.getPos();
                setState(false);
            }
            default -> {}
        }
    }

    @EventHandler
    public void onWorldRender(WorldRenderEvent e) {
        if (box != null) {
            Render3DUtil.drawBox(box, ColorUtil.getClientColor(), 1);
        }
    }

    @EventHandler
    public void onMove(MoveEvent e) {
        if (freezeSetting.isValue()) {
            e.setMovement(Vec3d.ZERO);
        }
    }

    @EventHandler
    public void onInput(InputEvent e) {
        float speed = speedSetting.getValue();
        double[] motion = MovingUtil.calculateDirection(e.forward(), e.sideways(), speed);

        prevPos = pos;
        pos = pos.add(motion[0], e.getInput().jump() ? speed : e.getInput().sneak() ? -speed : 0, motion[1]);

        e.inputNone();
    }

    @EventHandler
    public void onCameraPosition(CameraPositionEvent e) {
        e.setPos(MathUtil.interpolate(prevPos, pos));
        mc.options.setPerspective(Perspective.FIRST_PERSON);
    }

    @EventHandler
    public void onTick(TickEvent e) {
        if (freezeSetting.isValue() && frozenPos != null) {
            mc.player.setPosition(frozenPos);
        }
    }
}
