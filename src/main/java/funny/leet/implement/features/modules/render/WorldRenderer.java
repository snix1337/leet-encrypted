package funny.leet.implement.features.modules.render;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.MultiSelectSetting;
import funny.leet.api.feature.module.setting.implement.ValueSetting;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.other.Instance;
import funny.leet.implement.events.render.FogEvent;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class WorldRenderer extends Module {
    public static WorldRenderer getInstance() {
        return Instance.get(WorldRenderer.class);
    }

    public final MultiSelectSetting modeSetting = new MultiSelectSetting("World Setting", "Allows you to customize world")
            .value("Time", "Fog");

    public final ValueSetting timeSetting = new ValueSetting("Time", "Sets the value of the time")
            .setValue(12).range(0, 24).visible(() -> modeSetting.isSelected("Time"));

    public final ValueSetting distanceSetting = new ValueSetting("Fog Distance", "Sets the value of the time")
            .setValue(100).range(20, 200).visible(() -> modeSetting.isSelected("Fog"));

    public WorldRenderer() {
        super("WorldRenderer", "World Renderer", ModuleCategory.RENDER);
        setup(modeSetting, timeSetting, distanceSetting);
    }

    @Override
    public void deactivate() {
        super.deactivate();
    }

    @EventHandler
    public void onFog(FogEvent e) {
        if (modeSetting.isSelected("Fog")) {
            e.setDistance(distanceSetting.getValue());
            e.setColor(ColorUtil.getClientColor());
            e.cancel();
        }
    }
}
