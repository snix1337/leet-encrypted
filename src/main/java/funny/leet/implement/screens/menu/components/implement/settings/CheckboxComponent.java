package funny.leet.implement.screens.menu.components.implement.settings;

import net.minecraft.client.gui.DrawContext;
import obf.uscate.annotations.Compile;
import funny.leet.api.feature.module.setting.implement.BooleanSetting;
import funny.leet.api.system.font.Fonts;
import funny.leet.common.util.other.StringUtil;
import funny.leet.implement.screens.menu.components.implement.other.CheckComponent;

import static funny.leet.api.system.font.Fonts.Type.BOLD;

public class CheckboxComponent extends AbstractSettingComponent {
    private final CheckComponent checkComponent = new CheckComponent();
    private final BooleanSetting setting;

    public CheckboxComponent(BooleanSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        String wrapped = StringUtil.wrap(setting.getDescription(), 100, 12);
        height = (int) (18 + Fonts.getSize(12).getStringHeight(wrapped) / 3);

        Fonts.getSize(14, BOLD).drawString(context.getMatrices(), setting.getName(), x + 9, y + 6, 0xFFD4D6E1);
        Fonts.getSize(12).drawString(context.getMatrices(), wrapped, x + 9, y + 15, 0xFF878894);

        ((CheckComponent) checkComponent.position(x + width - 16, y + 7.5F))
                .setRunnable(() -> setting.setValue(!setting.isValue()))
                .setState(setting.isValue())
                .render(context, mouseX, mouseY, delta);
    }

    @Compile
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        checkComponent.mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
