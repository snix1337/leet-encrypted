package funny.leet.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import funny.leet.api.system.font.FontRenderer;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.world.ServerUtil;
import funny.leet.common.util.math.MathUtil;
import funny.leet.common.util.entity.MovingUtil;
import funny.leet.api.feature.draggable.AbstractDraggable;
import funny.leet.api.system.font.Fonts;
import funny.leet.common.util.entity.PlayerIntersectionUtil;

import java.util.Objects;

public class PlayerInfo extends AbstractDraggable {

    public PlayerInfo() {
        super("Player Info", 0, 0, 60, 0,false);
    }

    @Override
    public void drawDraggable(DrawContext context) {
        int offset = PlayerIntersectionUtil.isChat(mc.currentScreen) ? -13 : 0;
        BlockPos blockPos = Objects.requireNonNull(mc.player).getBlockPos();
        FontRenderer font = Fonts.getSize(15);

        setY(window.getScaledHeight() + offset);

        String tps = "tps: " + Formatting.RED + MathUtil.round(ServerUtil.TPS,0.1) + Formatting.RESET + "\n";
        String bps = "bps: " + Formatting.GOLD + MathUtil.round(MovingUtil.getSpeedSqrt(mc.player) * 20.0F, 0.1F) + Formatting.RESET + "\n";
        String xyz = "xyz: " + Formatting.GREEN + blockPos.getX() + Formatting.RESET + ", " + Formatting.GREEN + blockPos.getY() + Formatting.RESET + ", " + Formatting.GREEN + blockPos.getZ() + Formatting.RESET;
        font.drawString(context.getMatrices(), tps + bps + xyz,3,getY() - 24, ColorUtil.getText());
    }
}

/*
package funny.leet.implement.features.draggables;

import funny.leet.common.util.other.Instance;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import funny.leet.api.system.font.FontRenderer;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.world.ServerUtil;
import funny.leet.common.util.math.MathUtil;
import funny.leet.common.util.entity.MovingUtil;
import funny.leet.api.feature.draggable.AbstractDraggable;
import funny.leet.api.system.font.Fonts;
import funny.leet.common.util.entity.PlayerIntersectionUtil;

import java.util.Objects;

public class PlayerInfo extends AbstractDraggable {
    public static PlayerInfo getInstance() {
        return Instance.getDraggable(PlayerInfo.class);
    }

    public PlayerInfo() {
        super("Player Info", 5, 5, 60, 30, true); // Initial position: top-left (5, 5)
    }

    @Override
    public void drawDraggable(DrawContext context) {
        if (mc.player == null) return;

        boolean offset = PlayerIntersectionUtil.isChat(mc.currentScreen);
        BlockPos blockPos = Objects.requireNonNull(mc.player).getBlockPos();
        FontRenderer font = Fonts.getSize(15);

        // Calculate strings
        String bps = "BPS: " + Formatting.RED + MathUtil.round(MovingUtil.getSpeedSqrt(mc.player) * 20.0F, 0.1F);
        String tps = "TPS: " + Formatting.GOLD + MathUtil.round(ServerUtil.TPS, 0.1);
        String xyz = "XYZ: " + Formatting.GREEN + blockPos.getX() + ", " + Formatting.GREEN + blockPos.getY() + ", " + Formatting.GREEN + blockPos.getZ();

        // Calculate dimensions
        float bpsWidth = font.getStringWidth(bps);
        float tpsWidth = font.getStringWidth(tps);
        float xyzWidth = font.getStringWidth(xyz);
        float maxWidth = Math.max(bpsWidth, Math.max(tpsWidth, xyzWidth));
        float fontHeight = font.getFont().getSize();
        float lineHeight = fontHeight + 2;
        float padding = 6;
        setWidth((int) (maxWidth + padding)); // Dynamic width based on widest string
        setHeight((int) (lineHeight * 3 + padding)); // 3 lines + padding

        // Adjust Y for chat offset
        float startY = offset ? getY() + 20 : getY();

        // Draw BPS
        float currentY = startY;
        blur.render(ShapeProperties.create(context.getMatrices(), getX(), currentY, bpsWidth + padding, fontHeight + 4)
                .round(3).softness(1).thickness(2).outlineColor(ColorUtil.getOutline()).color(ColorUtil.getRect(0.7F)).build());
        font.drawString(context.getMatrices(), bps, getX() + 3, currentY + 2, ColorUtil.getText());
        currentY += lineHeight;

        // Draw TPS
        blur.render(ShapeProperties.create(context.getMatrices(), getX(), currentY, tpsWidth + padding, fontHeight + 4)
                .round(3).softness(1).thickness(2).outlineColor(ColorUtil.getOutline()).color(ColorUtil.getRect(0.7F)).build());
        font.drawString(context.getMatrices(), tps, getX() + 3, currentY + 2, ColorUtil.getText());
        currentY += lineHeight;

        // Draw XYZ
        blur.render(ShapeProperties.create(context.getMatrices(), getX(), currentY, xyzWidth + padding, fontHeight + 4)
                .round(3).softness(1).thickness(2).outlineColor(ColorUtil.getOutline()).color(ColorUtil.getRect(0.7F)).build());
        font.drawString(context.getMatrices(), xyz, getX() + 3, currentY + 2, ColorUtil.getText());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOver(mouseX, mouseY)) { // Left click
            setDragging(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            setDragging(false);
        }
        return false;
    }

    public void mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isDragging() && button == 0) {
            float newX = (float) (getX() + deltaX);
            float newY = (float) (getY() + deltaY);
            // Clamp to screen bounds using MathHelper
            newX = MathHelper.clamp(newX, 0, window.getScaledWidth() - getWidth());
            newY = MathHelper.clamp(newY, 0, window.getScaledHeight() - getHeight());
            setX((int) newX);
            setY((int) newY);
        }
    }

    private boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= getX() && mouseX <= getX() + getWidth() &&
               mouseY >= getY() && mouseY <= getY() + getHeight();
    }
}
 */
