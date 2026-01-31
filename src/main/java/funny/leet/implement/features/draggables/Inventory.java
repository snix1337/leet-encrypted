package funny.leet.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import funny.leet.api.feature.draggable.AbstractDraggable;
import funny.leet.api.system.font.FontRenderer;
import funny.leet.api.system.font.Fonts;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.entity.PlayerIntersectionUtil;
import funny.leet.common.util.render.Render2DUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Inventory extends AbstractDraggable {

    private final List<ItemStack> stacks = new ArrayList<>();

    public Inventory() {
        super("Inventory", 390, 10, 123, 60, true); // Оригинальный размер
    }

    @Override
    public boolean visible() {
        return stacks.stream().anyMatch(stack -> !stack.isEmpty()) || PlayerIntersectionUtil.isChat(mc.currentScreen);
    }

    @Override
    public void tick() {
        stacks.clear();
        IntStream.range(9, 36).mapToObj(i -> mc.player.getInventory().getStack(i)).forEach(stacks::add);
    }

    @Override
    public void drawDraggable(DrawContext context) {
        if (stacks.isEmpty()) return;

        MatrixStack matrix = context.getMatrices();
        FontRenderer fontTitle = Fonts.getSize(19, Fonts.Type.BOLD);

        float blurSoftness = 1.2F;

        // заголовок
        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth() + 8, 17.5F)
                .round(6,6,6,6)
                .softness(blurSoftness)
                .thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F))
                .color(ColorUtil.multAlpha(ColorUtil.BLACK, 0.65f))
                .build());

        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth() + 8, 17.5F)
                .round(6,6,6,6)
                .softness(blurSoftness)
                .thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F))
                .color(ColorUtil.multAlpha(ColorUtil.getClientColor(), 0.07f))
                .build());

        // инвентарь
        blur.render(ShapeProperties.create(matrix, getX(), getY() + 17.5F, getWidth() + 8, getHeight() - 17.5F)
                .round(6,6,6,6)
                .softness(blurSoftness)
                .thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F))
                .color(ColorUtil.getRect(0.7F))
                .build());

        // заголовок по центру
        float centerX = getX() + getWidth() / 1.89F;
        fontTitle.drawString(matrix, getName(),
                (int) (centerX - fontTitle.getStringWidth(getName()) / 2.0F),
                getY() + 5.7F,
                ColorUtil.getText());

        // иконки: 3 ряда по 9
        float itemSize = 13;
        float spacing = 1;
        float startX = getX() + 4;
        float startY = getY() + 20;

        int index = 0;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                if (index >= stacks.size()) break;
                ItemStack stack = stacks.get(index++);
                if (stack.isEmpty()) continue;

                float x = startX + col * (itemSize + spacing);
                float y = startY + row * (itemSize + spacing);

                Render2DUtil.defaultDrawStack(context, stack, x, y, false, true, 0.5F);
            }
        }
    }
}
