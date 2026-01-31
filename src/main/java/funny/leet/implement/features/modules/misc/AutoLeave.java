package funny.leet.implement.features.modules.misc;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.text.Text;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.MultiSelectSetting;
import funny.leet.api.feature.module.setting.implement.SelectSetting;
import funny.leet.api.feature.module.setting.implement.ValueSetting;
import funny.leet.api.repository.friend.FriendUtils;
import funny.leet.common.util.world.ServerUtil;
import funny.leet.implement.events.player.TickEvent;
import funny.leet.implement.features.draggables.Notifications;
import funny.leet.implement.features.draggables.StaffList;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AutoLeave extends Module {
    SelectSetting leaveType = new SelectSetting("Leave Type", "Allows you to select the leave type")
            .value("Hub", "Main Menu");

    MultiSelectSetting triggerSetting = new MultiSelectSetting("Triggers", "Select in which case you will exit")
            .value("Players", "Staff");

    ValueSetting distanceSetting = new ValueSetting("Max Distance", "Maximum distance for triggering auto leave")
            .setValue(10).range(5, 40).visible(() -> triggerSetting.isSelected("Players"));

    public AutoLeave() {
        super("AutoLeave", "Auto Leave", ModuleCategory.MISC);
        setup(leaveType, triggerSetting, distanceSetting);
    }

    
    @EventHandler
    public void onTick(TickEvent e) {
        if (ServerUtil.isPvp()) return;

        if (triggerSetting.isSelected("Players"))
            mc.world.getPlayers().stream().filter(p -> mc.player.distanceTo(p) < distanceSetting.getValue() && mc.player != p && !FriendUtils.isFriend(p))
                    .findFirst().ifPresent(p -> leave(p.getName().copy().append(" - Появился рядом " + mc.player.distanceTo(p) + "м")));
        if (triggerSetting.isSelected("Staff") && !StaffList.getInstance().list.isEmpty())
            leave(Text.of("Стафф на сервере"));
    }

    
    public void leave(Text text) {
        switch (leaveType.getSelected()) {
            case "Hub" -> {
                Notifications.getInstance().addList(Text.of("[AutoLeave] ").copy().append(text), 10000);
                mc.getNetworkHandler().sendChatCommand("hub");
            }
            case "Main Menu" ->
                    mc.getNetworkHandler().getConnection().disconnect(Text.of("[Auto Leave] \n").copy().append(text));
        }
        setState(false);
    }

}
