package funny.leet.implement.screens.menu.components.implement.settings;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import funny.leet.api.feature.module.setting.implement.ValueSetting;
import funny.leet.api.system.font.Fonts;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.math.MathUtil;
import funny.leet.common.util.other.StringUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static funny.leet.api.system.font.Fonts.Type.BOLD;

public class ValueComponent extends AbstractSettingComponent {
    public static final int SLIDER_WIDTH = 45;

    private final ValueSetting setting;
    private boolean dragging;
    private double animation;

    public ValueComponent(ValueSetting setting) {
        super(setting);
        this.setting = setting;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrix = context.getMatrices();

        String wrapped = StringUtil.wrap(setting.getDescription(), 70, 12);
        height = (int) (18 + Fonts.getSize(12).getStringHeight(wrapped) / 3);

        String value = String.valueOf(setting.getValue());

        Fonts.getSize(12, BOLD).drawString(matrix, value, x + width - 9 - Fonts.getSize(12).getStringWidth(value), y + 8, ColorUtil.getClientColor());

        changeValue(getDifference(mouseX, matrix));

        Fonts.getSize(14, BOLD).drawString(matrix, setting.getName(), x + 9, y + 6, 0xFFD4D6E1);
        Fonts.getSize(12).drawString(matrix, wrapped, x + 9, y + 15, 0xFF878894);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        dragging = MathUtil.isHovered(mouseX, mouseY, x + width - SLIDER_WIDTH - 9, y + 13, SLIDER_WIDTH, 3) && button == 0;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private float getDifference(int mouseX, MatrixStack matrix) {
        float percentValue = SLIDER_WIDTH * (setting.getValue() - setting.getMin()) / (setting.getMax() - setting.getMin()),
                difference = MathHelper.clamp(mouseX - (x + width - SLIDER_WIDTH - 9), 0, SLIDER_WIDTH);

        animation = MathUtil.interpolate(animation, percentValue);

        rectangle.render(ShapeProperties.create(matrix, x + width - SLIDER_WIDTH - 9, y + 13, (float) animation, 2F)
                .round(0.75F)
                .color(ColorUtil.getClientColor())
                .build());

        return difference;
    }

    private void changeValue(float difference) {
        BigDecimal bd = BigDecimal.valueOf((difference / SLIDER_WIDTH) * (setting.getMax() - setting.getMin()) + setting.getMin())
                .setScale(2, RoundingMode.HALF_UP);

        if (dragging) {
            float value = difference == 0 ? setting.getMin() : bd.floatValue();
            if (setting.isInteger()) value = (int) value;
            setting.setValue(value);
        }
    }
}
