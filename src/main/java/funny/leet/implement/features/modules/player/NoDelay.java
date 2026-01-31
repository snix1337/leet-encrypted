package funny.leet.implement.features.modules.player;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import obf.uscate.annotations.Compile;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.setting.implement.MultiSelectSetting;
import funny.leet.common.util.other.Instance;
import funny.leet.implement.events.player.TickEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoDelay extends Module {
    public static NoDelay getInstance() {
        return Instance.get(NoDelay.class);
    }

    public MultiSelectSetting ignoreSetting = new MultiSelectSetting("Type", "Allows the actions you choose")
            .value("Jump", "Right Click", "Break CoolDown");

    public NoDelay() {
        super("NoDelay", "No Delay", ModuleCategory.PLAYER);
        setup(ignoreSetting);
    }

    @Compile
    @EventHandler
    public void onTick(TickEvent e) {
        if (ignoreSetting.isSelected("Break CoolDown")) mc.interactionManager.blockBreakingCooldown = 0;
        if (ignoreSetting.isSelected("Jump")) mc.player.jumpingCooldown = 0;
        if (ignoreSetting.isSelected("Right Click")) mc.itemUseCooldown = 0;
    }
}