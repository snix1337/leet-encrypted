package funny.leet.implement.features.modules.player;

import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.BindSetting;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChestSaver extends Module {
    HandledScreen<?> screen;
    private Screen bufferedScreen;
    BindSetting openBindSetting = new BindSetting("Open Bind", "Opens the ender chest");
    BindSetting foldBindSetting = new BindSetting("Fold Items", "Puts all items in the ender chest");

    public ChestSaver() {
        super("ChestSaver", "Chest Saver", ModuleCategory.PLAYER);
        setup(openBindSetting, foldBindSetting);
    }

    @Override
    public void activate() {
        bufferedScreen = null;
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (isState()) onUpdate();
        });
    }

    @Override
    public void deactivate() {
        if (bufferedScreen != null) {
            mc.setScreen(bufferedScreen);
            bufferedScreen = null;
        }
    }

    public void onUpdate() {
        if (mc.player != null && mc.currentScreen instanceof HandledScreen && bufferedScreen == null) {
            bufferedScreen = mc.currentScreen;
            mc.setScreen(null);
        }
    }
}
