package funny.leet.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.Registries;
import funny.leet.api.feature.draggable.AbstractDraggable;
import funny.leet.api.system.animation.Animation;
import funny.leet.api.system.animation.Direction;
import funny.leet.api.system.animation.implement.DecelerateAnimation;
import funny.leet.api.system.font.FontRenderer;
import funny.leet.api.system.font.Fonts;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.math.MathUtil;
import funny.leet.common.util.other.Instance;
import funny.leet.common.util.math.StopWatch;
import funny.leet.common.util.other.StringUtil;
import funny.leet.common.util.entity.PlayerIntersectionUtil;
import funny.leet.common.util.render.Render2DUtil;
import funny.leet.implement.events.packet.PacketEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CoolDowns extends AbstractDraggable {

    public static CoolDowns getInstance() {
        return Instance.getDraggable(CoolDowns.class);
    }

    public final List<CoolDown> list = new ArrayList<>();

    public CoolDowns() {
        super("Cool Downs", 120, 10, 80, 23, true);
    }

    @Override
    public boolean visible() {
        return !list.isEmpty() || PlayerIntersectionUtil.isChat(mc.currentScreen);
    }

    @Override
    public void tick() {
        list.removeIf(c -> c.anim.isFinished(Direction.BACKWARDS));
        for (CoolDown coolDown : list) {
            if (!Objects.requireNonNull(mc.player).getItemCooldownManager().isCoolingDown(coolDown.item.getDefaultStack())) {
                coolDown.anim.setDirection(Direction.BACKWARDS);
            } else {
                coolDown.time.reset();
            }
        }
    }

    @Override
    public void packet(PacketEvent e) {
        if (PlayerIntersectionUtil.nullCheck()) return;
        switch (e.getPacket()) {
            case CooldownUpdateS2CPacket c -> {
                Item item = Registries.ITEM.get(c.cooldownGroup());
                list.stream()
                        .filter(cd -> cd.item.equals(item))
                        .forEach(cd -> cd.anim.setDirection(Direction.BACKWARDS));
                if (c.cooldown() != 0) {
                    list.add(new CoolDown(
                            item,
                            new StopWatch().setMs(-c.cooldown() * 50L),
                            new DecelerateAnimation().setMs(150).setValue(1.0F)
                    ));
                }
            }
            case PlayerRespawnS2CPacket p -> list.clear();
            default -> {}
        }
    }

    @Override
    public void drawDraggable(DrawContext context) {
        MatrixStack matrix = context.getMatrices();
        FontRenderer font = Fonts.getSize(19, Fonts.Type.BOLD);
        FontRenderer fontCoolDown = Fonts.getSize(17, Fonts.Type.DEFAULT);
        float spacing = 1.0F;
        float blurSoftness = 1.5F;

        float headerHeight = 17.5F;
        float titleY = getY() + 5.7F;

        int bgColor = ColorUtil.multAlpha(ColorUtil.BLACK, 0.65f);
        int accentColor = ColorUtil.multAlpha(ColorUtil.getClientColor(), 0.07f);
        int outlineColor = ColorUtil.getOutline(0.5F);

        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth(), headerHeight)
                .round(6).softness(blurSoftness).thickness(2)
                .outlineColor(outlineColor).color(bgColor).build());

        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth(), headerHeight)
                .round(6).softness(blurSoftness).thickness(2)
                .outlineColor(outlineColor).color(accentColor).build());

        float centerX = getX() + getWidth() / 2.0F;
        font.drawString(matrix, getName(), (int)(centerX - font.getStringWidth(getName()) / 2.0F), titleY, ColorUtil.getText());

        int offset = 23;
        int maxWidth = 80;

        for (CoolDown coolDown : list) {
            float animation = coolDown.anim.getOutput().floatValue();
            float centerY = getY() + offset;
            int timeLeft = -coolDown.time.elapsedTime() / 1000;
            String name = coolDown.item.getDefaultStack().getName().getString();
            String duration = timeLeft > 0 ? StringUtil.getDuration(timeLeft) : "";

            blur.render(ShapeProperties.create(matrix, getX(), getY() + offset, getWidth(), 14)
                    .round(5).softness(blurSoftness).thickness(2)
                    .outlineColor(outlineColor).color(ColorUtil.getRect(0.7F)).build());

            MathUtil.scale(matrix, centerX, centerY, 1, animation, () -> {
                float animRed = timeLeft > 0 && timeLeft <= 5 ? MathUtil.blinking(1000, 8) : 1.0F;

                Render2DUtil.defaultDrawStack(context, coolDown.item.getDefaultStack(), getX() + 5, (int)centerY + 3, false, false, 0.5F);
                fontCoolDown.drawString(matrix, name, getX() + 20, centerY + 4, ColorUtil.getText());

                if (timeLeft > 0) {
                    fontCoolDown.drawString(matrix, duration,
                            getX() + getWidth() - 6 - fontCoolDown.getStringWidth(duration),
                            centerY + 4,
                            ColorUtil.multRed(ColorUtil.getText(), animRed));
                }
            });

            int width = (int)fontCoolDown.getStringWidth(name + (timeLeft > 0 ? " " + duration : "")) + 32;
            maxWidth = Math.max(width, maxWidth);
            offset += (int)(14 * animation + spacing);
        }

        setWidth(maxWidth);
        setHeight(list.isEmpty() ? (int)headerHeight : offset);
    }

    public record CoolDown(Item item, StopWatch time, Animation anim) {}
}
