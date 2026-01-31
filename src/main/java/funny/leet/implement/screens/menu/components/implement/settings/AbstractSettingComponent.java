package funny.leet.implement.screens.menu.components.implement.settings;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import funny.leet.api.feature.module.setting.Setting;
import funny.leet.implement.screens.menu.components.AbstractComponent;

@Getter
@RequiredArgsConstructor
public abstract class AbstractSettingComponent extends AbstractComponent {
    private final Setting setting;
}
