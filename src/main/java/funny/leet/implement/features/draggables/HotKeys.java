package funny.leet.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import funny.leet.api.feature.draggable.AbstractDraggable;
import funny.leet.api.feature.module.Module;
import funny.leet.api.system.font.FontRenderer;
import funny.leet.api.system.font.Fonts;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.math.MathUtil;
import funny.leet.common.util.other.StringUtil;
import funny.leet.common.util.entity.PlayerIntersectionUtil;
import funny.leet.common.util.render.Render2DUtil;
import funny.leet.core.Main;

import java.util.ArrayList;
import java.util.List;

public class HotKeys extends AbstractDraggable {
    private List<Module> keysList = new ArrayList<>();
    private float dividerHeight = 8.0F;

    public HotKeys() {
        super("Hot Keys", 300, 10, 80, 23, true);
    }

    @Override
    public boolean visible() {
        return !keysList.isEmpty() || PlayerIntersectionUtil.isChat(mc.currentScreen);
    }

    @Override
    public void tick() {
        keysList = Main.getInstance().getModuleProvider().getModules().stream()
                .filter(module -> module.getAnimation().getOutput().floatValue() != 0 && module.getKey() != -1)
                .toList();
    }

    @Override
    public void drawDraggable(DrawContext e) {
        MatrixStack matrix = e.getMatrices();
        float centerX = getX() + getWidth() / 2F;

        FontRenderer font = Fonts.getSize(19, Fonts.Type.BOLD);
        FontRenderer fontModule = Fonts.getSize(17, Fonts.Type.DEFAULT);
        float spacing = 4.5F;

        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth(), 17.5F)
                .round(6,6,6,6).softness(1).thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F)).color(ColorUtil.multAlpha(ColorUtil.BLACK, 0.65f)).build());
        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth(), 17.5F)
                .round(6,6,6,6).softness(1).thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F)).color(ColorUtil.multAlpha(ColorUtil.getClientColor(), 0.07f)).build());
        font.drawString(matrix, "Hot Keys", (int) (centerX - font.getStringWidth(getName()) / 2), getY() + 5.7F, ColorUtil.getText());

        int offset = 23;
        int maxWidth = 80;

        if (!keysList.isEmpty()) {
            for (Module function : keysList) {
                String bind = StringUtil.getBindName(function.getKey());
                float centerY = getY() + offset;
                float animation = function.getAnimation().getOutput().floatValue();
                float width = fontModule.getStringWidth(function.getName()) + fontModule.getStringWidth(bind) + 19;

                blur.render(ShapeProperties.create(matrix, getX(), getY() + offset, getWidth(), 14)
                        .round(5,5,5,5).softness(1).thickness(2)
                        .outlineColor(ColorUtil.getOutline(0.5F)).color(ColorUtil.getRect(0.7F)).build());

                MathUtil.scale(matrix, centerX, centerY, 1, animation, () -> {
                    fontModule.drawString(matrix, function.getName(), getX() + 6, centerY + 4.5F, ColorUtil.getText());
                    float spriteX = getX() + getWidth() - 6 - fontModule.getStringWidth(bind) - 6;
                    Render2DUtil.drawRect(matrix, spriteX, centerY + 1.5F, 0.5F, dividerHeight, ColorUtil.getOutline(0.75F, 0.5F));
                    fontModule.drawString(matrix, bind, getX() + getWidth() - 6 - fontModule.getStringWidth(bind), centerY + 4.5F, ColorUtil.getText());
                });

                offset += (int) (animation * 11 + spacing);
                maxWidth = (int) Math.max(width, maxWidth);
            }
        }

        setWidth(maxWidth);
        setHeight(keysList.isEmpty() ? (int) 17.5F : offset);
    }
}

/*  дефолт отрисовка
public class HotKeys extends AbstractDraggable {
    private List<Module> keysList = new ArrayList<>();

    public HotKeys() {
        super("Hot Keys", 300, 10, 80, 23, true);
    }

    @Override
    public boolean visible() {
        return !keysList.isEmpty() || PlayerIntersectionUtil.isChat(mc.currentScreen);
    }

    @Override
    public void tick() {
        keysList = Main.getInstance().getModuleProvider().getModules().stream()
                .filter(module -> module.getAnimation().getOutput().floatValue() != 0 && module.getKey() != -1)
                .toList();
    }

    @Override
    public void drawDraggable(DrawContext e) {
        MatrixStack matrix = e.getMatrices();
        float centerX = getX() + getWidth() / 2F;

        FontRenderer font = Fonts.getSize(19, Fonts.Type.BOLD);
        FontRenderer fontModule = Fonts.getSize(17, Fonts.Type.DEFAULT);
        float spacing = 4.5F;

        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth(), 17.5F)
                .round(6,6,6,6).softness(1).thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F)).color(ColorUtil.multAlpha(ColorUtil.BLACK, 0.65f)).build());
        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth(), 17.5F)
                .round(6,6,6,6).softness(1).thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F)).color(ColorUtil.multAlpha(ColorUtil.getClientColor(), 0.07f)).build());
        font.drawString(matrix, "Hot Keys", (int) (centerX - font.getStringWidth(getName()) / 2), getY() + 5.7F, ColorUtil.getText());

        int offset = 23;
        int maxWidth = 80;

        if (!keysList.isEmpty()) {
            for (Module function : keysList) {
                String bind = "[" + StringUtil.getBindName(function.getKey()) + "]";
                float centerY = getY() + offset;
                float animation = function.getAnimation().getOutput().floatValue();
                float width = fontModule.getStringWidth(function.getName() + bind) + 15;

                blur.render(ShapeProperties.create(matrix, getX(), getY() + offset, getWidth(), 14)
                        .round(4.5F,4.5F,4.5F,4.5F).softness(1).thickness(2)
                        .outlineColor(ColorUtil.getOutline(0.5F)).color(ColorUtil.getRect(0.7F)).build());

                MathUtil.scale(matrix, centerX, centerY, 1, animation, () -> {
                    fontModule.drawString(matrix, function.getName(), getX() + 6, centerY + 4.5F, ColorUtil.getText());
                    fontModule.drawString(matrix, bind, getX() + getWidth() - 6 - fontModule.getStringWidth(bind), centerY + 4.5F, ColorUtil.getText());
                });

                offset += (int) (animation * 11 + spacing);
                maxWidth = (int) Math.max(width, maxWidth);
            }
        }

        setWidth(maxWidth);
        setHeight(keysList.isEmpty() ? (int) 17.5F : offset);
    }
}
*/
