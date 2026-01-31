package funny.leet.implement.features.modules.player;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.BooleanSetting;
import funny.leet.api.feature.module.setting.implement.TextSetting;
import funny.leet.api.repository.friend.FriendUtils;
import funny.leet.implement.events.render.TextFactoryEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NameProtect extends Module {
    TextSetting nameSetting = new TextSetting("Name", "Nickname that will be replaced with yours").setText("femboy fan!").setMax(16);
    BooleanSetting friendsSetting = new BooleanSetting("Friends","Hides friends' nicknames").setValue(true);

    public NameProtect() {
        super("NameProtect","Name Protect", ModuleCategory.PLAYER);
        setup(nameSetting, friendsSetting);
    }

    @EventHandler
    public void onTextFactory(TextFactoryEvent e) {
        e.replaceText(mc.getSession().getUsername(), nameSetting.getText());
        if (friendsSetting.isValue()) FriendUtils.getFriends().forEach(friend -> e.replaceText(friend.getName(), nameSetting.getText()));
    }
}
