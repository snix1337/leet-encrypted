package funny.leet.api.feature.module;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import funny.leet.api.system.animation.Animation;
import funny.leet.api.system.animation.Direction;
import funny.leet.api.system.animation.implement.DecelerateAnimation;
import funny.leet.api.system.sound.SoundManager;
import funny.leet.core.Main;
import funny.leet.api.feature.module.setting.SettingRepository;
import funny.leet.api.event.EventManager;
import funny.leet.common.QuickImports;
import funny.leet.implement.features.draggables.Notifications;
import funny.leet.implement.features.modules.render.Hud;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Module extends SettingRepository implements QuickImports {
    String name;
    String visibleName;
    ModuleCategory category;
    Animation animation = new DecelerateAnimation().setMs(150).setValue(1);

    public Module(String name, ModuleCategory category) {
        this.name = name;
        this.category = category;
        this.visibleName = name;
    }

    public Module(String name, String visibleName, ModuleCategory category) {
        this.name = name;
        this.visibleName = visibleName;
        this.category = category;
    }

    @NonFinal
    int key = GLFW.GLFW_KEY_UNKNOWN,type = 1;

    @NonFinal
    public boolean state;

    public void switchState() {
        setState(!state);
    }

    public void setState(boolean state) {
        animation.setDirection(state ? Direction.FORWARDS : Direction.BACKWARDS);
        if (state != this.state) {
            this.state = state;
            handleStateChange();
        }
    }

    private void handleStateChange() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player != null && mc.world != null) {
            if (state) {
                if (Hud.getInstance().notificationSettings.isSelected("Module Switch")) {
                    Notifications.getInstance().addList(Formatting.WHITE + visibleName + Formatting.RESET + "   was enabled", 1000, SoundManager.ENABLE_MODULE);
                }
                activate();
            } else {
                if (Hud.getInstance().notificationSettings.isSelected("Module Switch")) {
                    Notifications.getInstance().addList(Formatting.WHITE + visibleName + Formatting.RESET + "   was disabled", 1000, SoundManager.DISABLE_MODULE);
                }
                deactivate();
            }
        }
        toggleSilent(state);
    }

    private void toggleSilent(boolean activate) {
        EventManager eventManager = Main.getInstance().getEventManager();
        if (activate) {
            eventManager.register(this);
        } else {
            eventManager.unregister(this);
        }
    }

    public void activate() {
    }

    public void deactivate() {
    }
}
