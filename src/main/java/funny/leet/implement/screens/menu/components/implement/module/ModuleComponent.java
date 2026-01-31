package funny.leet.implement.screens.menu.components.implement.module;

import lombok.Getter;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ColorHelper;
import org.lwjgl.glfw.GLFW;
import obf.uscate.annotations.Compile;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.setting.SettingComponentAdder;
import funny.leet.api.system.font.Fonts;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.math.MathUtil;
import funny.leet.common.util.other.StringUtil;
import funny.leet.implement.screens.menu.components.AbstractComponent;
import funny.leet.implement.screens.menu.components.implement.other.CheckComponent;
import funny.leet.implement.screens.menu.components.implement.settings.AbstractSettingComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static funny.leet.api.system.font.Fonts.Type.BOLD;

@Getter
public class ModuleComponent extends AbstractComponent {
    private final List<AbstractSettingComponent> components = new ArrayList<>();

    private final CheckComponent checkComponent = new CheckComponent();

    private final Module module;
    private boolean binding;

    private void initialize() {
        new SettingComponentAdder().addSettingComponent(
                module.settings(),
                components
        );
    }

    public ModuleComponent(Module module) {
        this.module = module;
        initialize();
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        float blurSoftness = 1.0F + components.size() * 0.2F; // Динамическое размытие, как в Potions

        rectangle.render(ShapeProperties.create(context.getMatrices(), x, y, width, 18)
                .round(8,0,8,0).color(ColorUtil.getGuiRectColor2(1)).build());

        rectangle.render(ShapeProperties.create(context.getMatrices(), x, y, width, height = getComponentHeight())
                .round(8).softness(1).thickness(2.2F).outlineColor(0x902d2e41).color(0x002d2e41).build());

        blur.render(ShapeProperties.create(context.getMatrices(), x, y, width, 17.5F) // Темный блюр верхней части
                .round(8,0,8,0).softness(blurSoftness).thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F)).color(ColorUtil.multAlpha(ColorUtil.BLACK, 0.95f)).build()); // Увеличен альфа-канал до 0.85 для темного эффекта
        blur.render(ShapeProperties.create(context.getMatrices(), x, y, width, 17.5F)
                .round(8,0,8,0).softness(blurSoftness).thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F)).color(ColorUtil.multAlpha(ColorUtil.BLACK, 0.15f)).build()); // Темный оттенок вместо getClientColor

        blur.render(ShapeProperties.create(context.getMatrices(), x, y + 18, width, height - 18) // Блюр нижней части
                .round(0,8,0,8).softness(1).thickness(2).outlineColor(ColorUtil.getOutline(0.5F)).color(ColorUtil.getRect(0.9F)).build());

        Fonts.getSize(14, BOLD).drawString(context.getMatrices(), module.getVisibleName(), x + 10, y + 8, 0xFFD4D6E1);

        Fonts.getSize(14, BOLD).drawString(context.getMatrices(), "Enable", x + 9, y + 27, 0xFFD4D6E1);
        Fonts.getSize(12, BOLD).drawString(context.getMatrices(), "Enables the " + module.getVisibleName().toLowerCase() + " feature.", x + 9, y + 36, 0xFF878894);

        ((CheckComponent) checkComponent.position(x + width - 16, y + 28.5F)).setRunnable(module::switchState).setState(module.isState()).render(context, mouseX, mouseY, delta);

        drawBind(context);

        float offset = y + 42;
        for (int i = components.size() - 1; i >= 0; i--) {
            AbstractSettingComponent component = components.get(i);
            Supplier<Boolean> visible = component.getSetting().getVisible();

            if (visible != null && !visible.get()) {
                continue;
            }

            component.x = x;
            component.y = offset + (getComponentHeight() - 46 - component.height);
            component.width = width;

            component.render(context, mouseX, mouseY, delta);

            offset -= component.height;
        }
    }

    @Compile
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean isAnyComponentHovered = components.stream().anyMatch(abstractComponent -> abstractComponent.isHover(mouseX, mouseY));

        if (isAnyComponentHovered) {
            components.forEach(abstractComponent -> {
                if (abstractComponent.isHover(mouseX, mouseY)) {
                    abstractComponent.mouseClicked(mouseX, mouseY, button);
                }
            });
            return super.mouseClicked(mouseX, mouseY, button);
        } else {
            String bindName = StringUtil.getBindName(module.getKey());
            float stringWidth = Fonts.getSize(12, BOLD).getStringWidth(bindName);
            if (MathUtil.isHovered(mouseX, mouseY, x + width - 15 - stringWidth, y + 8, stringWidth + 6, 9) && button == 0) {
                binding = !binding;
            } else if (binding) {
                module.setKey(button);
                binding = false;
            }
        }

        checkComponent.mouseClicked(mouseX, mouseY, button);
        components.forEach(abstractComponent -> abstractComponent.mouseClicked(mouseX, mouseY, button));
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean isHover(double mouseX, double mouseY) {
        for (AbstractComponent abstractComponent : components) {
            if (abstractComponent.isHover(mouseX, mouseY)) {
                return true;
            }
        }
        return MathUtil.isHovered(mouseX, mouseY, x, y, width, height);
    }


    @Override
    public void tick() {
        components.forEach(AbstractComponent::tick);
        super.tick();
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        components.forEach(abstractComponent -> abstractComponent.mouseDragged(mouseX, mouseY, button, deltaX, deltaY));
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }


    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        components.forEach(abstractComponent -> abstractComponent.mouseReleased(mouseX, mouseY, button));
        return super.mouseReleased(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        components.forEach(abstractComponent -> abstractComponent.mouseScrolled(mouseX, mouseY, amount));
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Compile
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int key = keyCode == GLFW.GLFW_KEY_DELETE ? -1 : keyCode;
        if (binding) {
            module.setKey(key);
            binding = false;
        }
        components.forEach(abstractComponent -> abstractComponent.keyPressed(keyCode, scanCode, modifiers));
        return super.keyPressed(keyCode, scanCode, modifiers);
    }


    @Override
    public boolean charTyped(char chr, int modifiers) {
        components.forEach(abstractComponent -> abstractComponent.charTyped(chr, modifiers));
        return super.charTyped(chr, modifiers);
    }


    public int getComponentHeight() {
        float offsetY = 0;
        for (AbstractSettingComponent component : components) {
            Supplier<Boolean> visible = component.getSetting().getVisible();

            if (visible != null && !visible.get()) {
                continue;
            }

            offsetY += component.height;
        }
        return (int) (offsetY + 46);
    }


    private void drawBind(DrawContext context) {
        String bindName = StringUtil.getBindName(module.getKey());
        String name = binding ? "(" + bindName + ") ..." : bindName;
        float stringWidth = Fonts.getSize(12, BOLD).getStringWidth(name);

        rectangle.render(ShapeProperties.create(context.getMatrices(), x + width - stringWidth - 15, y + 4.5F, stringWidth + 6, 9)
                .round(2).thickness(2).outlineColor(ColorUtil.getOutline()).color(ColorUtil.getGuiRectColor(1)).build());

        int bindingColor = ColorHelper.getArgb(255, 135, 136, 148);
        Fonts.getSize(12, BOLD).drawString(context.getMatrices(), name, x + width - 12 - stringWidth, y + 8, bindingColor);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleComponent that = (ModuleComponent) o;
        return module.equals(that.module);
    }


    @Override
    public int hashCode() {
        return Objects.hash(module);
    }
}