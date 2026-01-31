package funny.leet.implement.features.modules.movement;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.common.util.entity.PlayerInventoryUtil;
import funny.leet.implement.events.player.TickEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ElytraMotion extends Module {
    boolean lastTick = false;
    int fireworkTick;

    public ElytraMotion() {
        super("ElytraMotion", "Elytra Motion", ModuleCategory.MOVEMENT);
    }

    @EventHandler
    public void onTick(TickEvent e) {
        int lastSlot = mc.player.getInventory().selectedSlot;
        if (mc.player.getInventory().getStack(38).getItem() == Items.ELYTRA && mc.player.isGliding() && mc.options.forwardKey.isPressed()) {
            if (PlayerInventoryUtil.boolHotbarItem(Items.FIREWORK_ROCKET)) {
                int fireworkSlot = PlayerInventoryUtil.searchHotbarItem(Items.FIREWORK_ROCKET);
                if (fireworkTick >= 10) {
                    lastTick = true;
                    mc.player.getInventory().selectedSlot = fireworkSlot;
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                    mc.player.getInventory().selectedSlot = lastSlot;
                    fireworkTick = 0;
                } else fireworkTick++;
                if (lastTick) {
                    mc.player.setVelocity(0, 0, 0);
                }
            }
        } else {
            fireworkTick = 10;
        }
    }

    public void deactivate() {
        fireworkTick = 10;
    }
}
