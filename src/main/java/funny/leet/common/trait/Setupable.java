package funny.leet.common.trait;

import funny.leet.api.feature.module.setting.Setting;

public interface Setupable {
    void setup(Setting... settings);
}