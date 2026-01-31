package funny.leet.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Arm;
import funny.leet.api.feature.draggable.AbstractDraggable;
import funny.leet.api.system.font.FontRenderer;
import funny.leet.api.system.font.Fonts;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.math.MathUtil;
import funny.leet.common.util.render.Render2DUtil;
import funny.leet.common.util.color.ColorUtil;

import java.util.Objects;
import java.util.stream.IntStream;

public class HotBar extends AbstractDraggable {

    private float selectItemX;

    public HotBar() {
        super("HotBar", 0, 50, 182, 22, false);
    }

    @Override
    public void drawDraggable(DrawContext context) {
        MatrixStack matrix = context.getMatrices();
        PlayerInventory inventory = Objects.requireNonNull(mc.player).getInventory();
        ItemStack offHand = mc.player.getOffHandStack();

        selectItemX = MathUtil.interpolateSmooth(1, selectItemX, inventory.selectedSlot * 20);
        setX((mc.getWindow().getScaledWidth() - getWidth()) / 2);
        setY(mc.getWindow().getScaledHeight() - 27);

        blur.render(ShapeProperties.create(matrix, getX() - 0.5F, getY() - 0.5F, getWidth() + 1, 23F)
                .round(5.5F).softness(1).thickness(2)
                .outlineColor(ColorUtil.getOutline())
                .color(ColorUtil.getRect(0.7F))
                .build());

        rectangle.render(ShapeProperties.create(matrix, getX() + selectItemX + 1, getY() + 1, 20, 20)
                .round(4.5F).thickness(2.9F)
                .outlineColor(ColorUtil.getClientColor())
                .color(ColorUtil.getRect(0))
                .build());

        IntStream.range(0, 9).forEach(i ->
                drawStack(context, inventory.main.get(i), getX() + i * 20 + 2, getY() + 2, false));

        if (!offHand.isEmpty()) {
            drawStack(context, offHand,
                    getX() + (mc.player.getMainArm().equals(Arm.RIGHT) ? -28 : 198),
                    getY() + 2, true);
        }

        if (!mc.player.isSpectator() && !mc.player.isCreative()) {
            drawExperienceBar(matrix);
        }

        drawOverlayInfo(matrix);
    }

    public void drawExperienceBar(MatrixStack matrix) {
        Fonts.getSize(16).drawCenteredString(matrix,
                mc.player.experienceLevel + "",
                mc.getWindow().getScaledWidth() / 2F,
                getY() - 9.5F,
                ColorUtil.GREEN);
    }

    public void drawOverlayInfo(MatrixStack matrix) {
        float scaledWidth = (float) mc.getWindow().getScaledWidth() / 2;
        float heightStart = mc.getWindow().getScaledHeight() - 75;
        float paddingX = 4;
        float paddingY = 3;
        FontRenderer font = Fonts.getSize(14, Fonts.Type.DEFAULT);

        if (mc.inGameHud.heldItemTooltipFade > 0 && mc.inGameHud.currentStack != null) {
            float alpha = ((float) mc.inGameHud.heldItemTooltipFade * 256.0F / 10.0F) / 255;
            Text text = mc.inGameHud.currentStack.getName();
            float width = font.getStringWidth(text);
            int x = (int) (scaledWidth - width / 2);

            MathUtil.setAlpha(alpha, () -> {
                blur.render(ShapeProperties.create(matrix, x - paddingX, heightStart - paddingY,
                                width + paddingX * 2, font.getStringHeight(text) / 2.15F + paddingY * 2)
                        .round(5.5F).softness(1).thickness(2)
                        .outlineColor(ColorUtil.getOutline(0.5F))
                        .color(ColorUtil.getRect(0.7F))
                        .build());
                font.drawText(matrix, text, x, heightStart + 2.5F);
            });
        }

        if (mc.inGameHud.overlayRemaining > 0 && mc.inGameHud.overlayMessage != null && !mc.inGameHud.overlayMessage.getString().isEmpty()) {
            float alpha = ((float) mc.inGameHud.overlayRemaining * 256.0F / 10.0F) / 255;
            Text text = mc.inGameHud.overlayMessage;
            float width = font.getStringWidth(text);
            int x = (int) (scaledWidth - width / 2);

            MathUtil.setAlpha(alpha, () -> {
                blur.render(ShapeProperties.create(matrix, x - paddingX, heightStart - paddingY - 17,
                                width + paddingX * 2, font.getStringHeight(text) / 2.15F + paddingY * 2)
                        .round(5.5F).softness(1).thickness(2)
                        .outlineColor(ColorUtil.getOutline(0.5F))
                        .color(ColorUtil.getRect(0.7F))
                        .build());
                font.drawText(matrix, text, x, heightStart - 14.5F);
            });
        }
    }

    public void drawStack(DrawContext context, ItemStack stack, float x, float y, boolean offHand) {
        if (offHand) {
            blur.render(ShapeProperties.create(context.getMatrices(), x - 2.5F, y - 2.5F, 23, 23)
                    .round(5.5F).softness(1).thickness(2)
                    .outlineColor(ColorUtil.getOutline(0.5F))
                    .color(ColorUtil.getRect(0.7F))
                    .build());
        }
        Render2DUtil.defaultDrawStack(context, stack, x, y, false, true, 1);
    }
}
