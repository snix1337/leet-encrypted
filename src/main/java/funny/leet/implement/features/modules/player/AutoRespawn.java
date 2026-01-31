package funny.leet.implement.features.modules.player;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.DeathMessageS2CPacket;
import obf.uscate.annotations.Compile;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.SelectSetting;
import funny.leet.api.event.EventHandler;
import funny.leet.common.util.world.ServerUtil;
import funny.leet.implement.events.packet.PacketEvent;
import funny.leet.implement.events.player.DeathScreenEvent;

@SuppressWarnings("all")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AutoRespawn extends Module {

    SelectSetting modeSetting = new SelectSetting("Mode", "Choose what will be used").value("FunTime Back", "Default");

    public AutoRespawn() {
        super("AutoRespawn", "Auto Respawn", ModuleCategory.PLAYER);
        setup(modeSetting);
    }

    @EventHandler
    public void onPacket(PacketEvent e) {
        switch (e.getPacket()) {
            case DeathMessageS2CPacket message when ServerUtil.getWorldType().equals("lobby") && modeSetting.isSelected("FunTime Back") -> {
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(1448, 1337, 228, false, false));
                mc.player.requestRespawn();
                mc.player.closeScreen();
            }
            default -> {
            }
        }
    }

    @Compile
    @EventHandler
    public void onDeathScreen(DeathScreenEvent e) {
        if (modeSetting.isSelected("Default")) {
            mc.player.requestRespawn();
            mc.setScreen(null);
        }
    }
}
