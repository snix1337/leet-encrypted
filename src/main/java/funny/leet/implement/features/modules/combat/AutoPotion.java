package funny.leet.implement.features.modules.combat;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.implement.events.player.TickEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AutoPotion extends Module {
    public AutoPotion() {
        super("AutoPotion", "Auto Potion", ModuleCategory.COMBAT);
        setup();
    }

    @EventHandler
    public void onTick(TickEvent e) {

    }
}
