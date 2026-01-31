package funny.leet.implement.screens.menu.components.implement.window.implement.settings.color;

import funny.leet.common.util.render.ScissorManager;
import funny.leet.core.Main;
import funny.leet.implement.screens.menu.components.implement.window.implement.settings.color.component.*;
import net.minecraft.client.gui.DrawContext;
import funny.leet.api.feature.module.setting.implement.ColorSetting;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.math.MathUtil;
import funny.leet.implement.screens.menu.components.AbstractComponent;
import funny.leet.implement.screens.menu.components.implement.window.AbstractWindow;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorWindow extends AbstractWindow {
    private final List<AbstractComponent> components = new ArrayList<>();

    private final HueComponent hueComponent;
    private final SaturationComponent saturationComponent;
    private final AlphaComponent alphaComponent;
    private final ColorEditorComponent colorEditorComponent;
    private final ColorPresetComponent colorPresetComponent;

    public ColorWindow(ColorSetting setting) {
        components.addAll(
                Arrays.asList(
                        hueComponent = new HueComponent(setting),
                        saturationComponent = new SaturationComponent(setting),
                        alphaComponent = new AlphaComponent(setting),
                        colorEditorComponent = new ColorEditorComponent(setting),
                        colorPresetComponent = new ColorPresetComponent(setting)
                )
        );
    }

    @Override
    public void drawWindow(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrix = context.getMatrices();

        blur.render(ShapeProperties.create(matrix, x, y + 19, width, height - 19)
                .round(0,8,0,8)
                .softness(1)
                .thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F))
                .color(ColorUtil.getRect(0.5F))
                .build());

        rectangle.render(ShapeProperties.create(matrix, x, y, width, height)
                .round(8).thickness(2).softness(1).outlineColor(ColorUtil.getOutline()).color(ColorUtil.getGuiRectColor(0.7F)).build());

        alphaComponent.position(x, y);
        hueComponent.position(x, y);
        saturationComponent.position(x, y);
        colorEditorComponent.position(x, y);

        height = ((ColorPresetComponent) colorPresetComponent.position(x, y))
                .getWindowHeight() - 4;

        components.forEach(component -> component.render(context, mouseX, mouseY, delta));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        draggable(MathUtil.isHovered(mouseX, mouseY, x, y, width, 17));
        components.forEach(component -> component.mouseClicked(mouseX, mouseY, button));
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        components.forEach(component -> component.mouseScrolled(mouseX, mouseY, amount));
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        components.forEach(component -> component.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }
}
