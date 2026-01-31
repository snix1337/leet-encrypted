package funny.leet.implement.screens.menu.components.implement.other;

import funny.leet.api.system.font.Fonts;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.implement.screens.menu.MenuScreen;
import funny.leet.implement.screens.menu.components.AbstractComponent;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

@Setter
@Accessors(chain = true)
public class BackgroundComponent extends AbstractComponent {

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        MatrixStack matrix = context.getMatrices();
        float gap = 2.0F;

        // Background blur
        blur.render(ShapeProperties.create(matrix, x - 2, y - 2, width - 9, height + 4)
                .round(14,14,14,14)
                .softness(1)
                .thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F, 1))
                .color(ColorUtil.multAlpha(ColorUtil.BLACK, 0.65f))
                .build());

        // Non-main panel (left sidebar)
        blur.render(ShapeProperties.create(matrix, x, y, 85, height)
                .round(10,10,12,12) // 9 9 10 10
                .softness(1)
                .thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F, 1))
                .color(ColorUtil.getRect(0.8F))
                .build());

        // Category panel (the smallest)
        blur.render(ShapeProperties.create(matrix, x + 85 + gap, y, width - 98 - gap, 28)
                .round(12,10,10,10) // 10 9 9 9
                .softness(1)
                .thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F, 1))
                .color(ColorUtil.getRect(0.8F))
                .build());

        // Main panel (below category panel)
        blur.render(ShapeProperties.create(matrix, x + 85 + gap, y + 28 + gap, width - 98 - gap, height - 28 - gap)
                .round(10,12,10,10) // 9 10 9 9
                .softness(1)
                .thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F, 1))
                .color(ColorUtil.getRect(0.8F))
                .build());

        // Category name text
        Fonts.getSize(18).drawString(matrix, MenuScreen.INSTANCE.getCategory().getReadableName(), x + 95, y + 12, 0xFFD4D6E1);
    }
}
