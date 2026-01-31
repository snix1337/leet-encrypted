package funny.leet.implement.features.draggables;

import funny.leet.common.util.other.StringUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import funny.leet.api.feature.draggable.AbstractDraggable;
import funny.leet.api.system.font.FontRenderer;
import funny.leet.api.system.font.Fonts;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.math.MathUtil;
import funny.leet.core.Main;

public class Watermark extends AbstractDraggable {
    private int fpsCount = 0;

    public Watermark() {
        super("Watermark", 10, 10, 92, 16, true);
    }

    @Override
    public void tick() {
        fpsCount = (int) MathUtil.interpolate(fpsCount, mc.getCurrentFps());
    }

    @Override
    public void drawDraggable(DrawContext e) {
        MatrixStack matrix = e.getMatrices();
        FontRenderer font = Fonts.getSize(15, Fonts.Type.DEFAULT);

        String offset = "      ";
        String name = Main.getInstance().getClientInfoProvider().clientName() + offset;
        String username = (mc.player != null ? mc.player.getName().getString() : "Unknown") + offset;
        String version = StringUtil.getUserRole() + offset;
        String fps = fpsCount + " fps";

        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth(), getHeight())
                .round(5.0F).softness(1).thickness(2).outlineColor(ColorUtil.getOutline()).color(ColorUtil.getRect(0.7F)).build());
        font.drawGradientString(matrix, name, getX() + 5, getY() + 6.5F, ColorUtil.fade(0), ColorUtil.fade(100));
        font.drawString(matrix, username, getX() + font.getStringWidth(name) + 5, getY() + 6.5F, ColorUtil.getText());
        font.drawGradientString(matrix, version, getX() + font.getStringWidth(name + username) + 5.5F, getY() + 6.5F, ColorUtil.fade(0), ColorUtil.fade(100));
        font.drawString(matrix, fps, getX() + font.getStringWidth(name + username + version) + 4.0F, getY() + 6.5F, ColorUtil.getText());
        rectangle.render(ShapeProperties.create(matrix, getX() + font.getStringWidth(name), getY() + 4, 0.5F, getHeight() - 8).color(ColorUtil.getOutline(0.75F, 0.5f)).build());
        rectangle.render(ShapeProperties.create(matrix, getX() + font.getStringWidth(name + username), getY() + 4, 0.5F, getHeight() - 8).color(ColorUtil.getOutline(0.75F, 0.5f)).build());
        rectangle.render(ShapeProperties.create(matrix, getX() + font.getStringWidth(name + username + version), getY() + 4, 0.5F, getHeight() - 8).color(ColorUtil.getOutline(0.75F, 0.5f)).build());
        setWidth((int) (font.getStringWidth(name + username + version + fps) + 9));
    }
}
