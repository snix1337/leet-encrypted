package funny.leet.implement.features.modules.render;

import dev.redstones.mediaplayerinfo.IMediaSession;
import funny.leet.implement.features.draggables.MediaPlayer;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.setting.implement.BindSetting;
import funny.leet.api.feature.module.setting.implement.ColorSetting;
import funny.leet.api.feature.module.setting.implement.MultiSelectSetting;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.common.util.other.Instance;
import funny.leet.implement.events.keyboard.KeyEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Hud extends Module {
    public static Hud getInstance() {
        return Instance.get(Hud.class);
    }

    public MultiSelectSetting interfaceSettings = new MultiSelectSetting("Elements", "Customize the interface elements")
                .value("Watermark", "Hot Keys", "Potions", "Staff List", "Target Hud", "Armor", "Cool Downs", "Inventory", "Player Info", "Boss Bars", "Notifications", "Score Board", "Tunes", "HotBar");

    public MultiSelectSetting notificationSettings = new MultiSelectSetting("Notifications", "Choose when the notification will appear")
            .value("Module Switch", "Staff Join", "Item Pick Up", "Auto Armor", "Break Shield").visible(()-> interfaceSettings.isSelected("Notifications"));

    public ColorSetting colorSetting = new ColorSetting("Client Theme", "Select your client's theme")
            .setColor(0xFF6C9AFD).presets(0xFF6C9AFD, 0xFF8C7FFF, 0xFFFFA576, 0xFFFF7B7B);

    BindSetting preSetting = new BindSetting("Previous Audio", "Turn on previous audio")
            .visible(()-> interfaceSettings.isSelected("Tunes"));

    BindSetting playSetting = new BindSetting("Stop/Play Audio",   "Stop/Play current audio")
            .visible(()-> interfaceSettings.isSelected("Tunes"));

    BindSetting nextSetting = new BindSetting("Next Audio","Turn on next audio")
            .visible(()-> interfaceSettings.isSelected("Tunes"));

    public Hud() {
        super("Hud", ModuleCategory.RENDER);
        setup(colorSetting, interfaceSettings, notificationSettings, preSetting, playSetting, nextSetting);
    }

    @EventHandler
    public void onKey(KeyEvent e) {
        IMediaSession session = MediaPlayer.getInstance().session;
        if (interfaceSettings.isSelected("Tunes") && session != null) {
            if (e.isKeyDown(preSetting.getKey(), true)) session.previous();
            if (e.isKeyDown(playSetting.getKey(), true)) session.playPause();
            if (e.isKeyDown(nextSetting.getKey(), true)) session.next();
        }
    }
}
