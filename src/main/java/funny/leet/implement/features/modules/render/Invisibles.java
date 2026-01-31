package funny.leet.implement.features.modules.render;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.ValueSetting;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.implement.events.render.EntityColorEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Invisibles extends Module {
    ValueSetting alphaSetting = new ValueSetting("Persent", "Player Persent").setValue(0.5f).range(0.1F, 1);

    public Invisibles() {
        super("Invisibles", "Invisibles", ModuleCategory.RENDER);
        setup(alphaSetting);
    }

    @EventHandler
    public void onEntityColor(EntityColorEvent e) {
        e.setColor(ColorUtil.multAlpha(e.getColor(), alphaSetting.getValue()));
        e.cancel();
    }

}
