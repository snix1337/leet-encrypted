package funny.leet.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.RemoveEntityStatusEffectS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Formatting;
import funny.leet.api.feature.draggable.AbstractDraggable;
import funny.leet.api.system.animation.Animation;
import funny.leet.api.system.animation.Direction;
import funny.leet.api.system.animation.implement.DecelerateAnimation;
import funny.leet.api.system.font.FontRenderer;
import funny.leet.api.system.font.Fonts;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.math.MathUtil;
import funny.leet.common.util.entity.PlayerIntersectionUtil;
import funny.leet.common.util.render.Render2DUtil;
import funny.leet.implement.events.packet.PacketEvent;

import java.util.*;

public class Potions extends AbstractDraggable {
    private final List<Potion> list = new ArrayList<>();
    private float dividerHeight = 8.0F;

    public Potions() {
        super("Potions", 210, 10, 80, 23, true);
    }

    @Override
    public boolean visible() {
        return !list.isEmpty() || PlayerIntersectionUtil.isChat(mc.currentScreen);
    }

    @Override
    public void tick() {
        list.removeIf(p -> p.anim.isFinished(Direction.BACKWARDS));
        for (Potion potion : list) {
            potion.effect.update(mc.player, null); // Обновляем эффект
            if (potion.effect.getDuration() <= 0 && !potion.effect.isInfinite()) {
                potion.anim.setDirection(Direction.BACKWARDS); // Анимация исчезновения при истечении
            }
        }
    }

    @Override
    public void packet(PacketEvent e) {
        switch (e.getPacket()) {
            case EntityStatusEffectS2CPacket effect -> {
                if (!PlayerIntersectionUtil.nullCheck() && effect.getEntityId() == Objects.requireNonNull(mc.player).getId()) {
                    RegistryEntry<StatusEffect> effectId = effect.getEffectId();
                    list.stream().filter(p -> p.effect.getEffectType().getIdAsString().equals(effectId.getIdAsString())).forEach(s -> s.anim.setDirection(Direction.BACKWARDS));
                    list.add(new Potion(new StatusEffectInstance(effectId, effect.getDuration(), effect.getAmplifier(), effect.isAmbient(), effect.shouldShowParticles(), effect.shouldShowIcon()), new DecelerateAnimation().setMs(150).setValue(1.0F)));
                }
            }
            case RemoveEntityStatusEffectS2CPacket effect -> list.stream().filter(s -> s.effect.getEffectType().getIdAsString().equals(effect.effect().getIdAsString())).forEach(s -> s.anim.setDirection(Direction.BACKWARDS));
            case PlayerRespawnS2CPacket p -> list.clear();
            case GameJoinS2CPacket p -> list.clear();
            default -> {}
        }
    }

    @Override
    public void drawDraggable(DrawContext context) {
        MatrixStack matrix = context.getMatrices();
        FontRenderer font = Fonts.getSize(19, Fonts.Type.BOLD);
        FontRenderer fontPotion = Fonts.getSize(17, Fonts.Type.DEFAULT);
        float spacing = 1.0F;

        float blurSoftness = 1.0F + list.size() * 0.2F;

        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth(), 17.5F)
                .round(6,6,6,6)
                .softness(blurSoftness)
                .thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F))
                .color(ColorUtil.multAlpha(ColorUtil.BLACK, 0.65f))
                .build());

        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth(), 17.5F)
                .round(6,6,6,6)
                .softness(blurSoftness)
                .thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F))
                .color(ColorUtil.multAlpha(ColorUtil.getClientColor(), 0.07f))
                .build());

        float centerX = getX() + getWidth() / 2.0F;
        font.drawString(matrix, getName(), (int) (centerX - font.getStringWidth(getName()) / 2.0F), getY() + 5.7F, ColorUtil.getText());

        int offset = 23;
        int maxWidth = 80;

        for (Potion potion : list) {
            StatusEffectInstance effect = potion.effect;
            float animation = potion.anim.getOutput().floatValue();
            float centerY = getY() + offset;
            int amplifier = effect.getAmplifier();

            String name = effect.getEffectType().value().getName().getString();
            String duration = getDuration(effect);
            String lvl = amplifier > 0 ? Formatting.RED + " " + (amplifier + 1) + Formatting.RESET : "";

            blur.render(ShapeProperties.create(matrix, getX(), getY() + offset, getWidth(), 14)
                    .round(5,5,5,5)
                    .softness(blurSoftness)
                    .thickness(2)
                    .outlineColor(ColorUtil.getOutline(0.5F))
                    .color(ColorUtil.getRect(0.7F))
                    .build());

            MathUtil.scale(matrix, centerX, centerY, 1, animation, () -> {
                float animRed = effect.getDuration() != -1 && effect.getDuration() <= 120 ? MathUtil.blinking(1000, 8) : 1;
                Render2DUtil.drawSprite(matrix, mc.getStatusEffectSpriteManager().getSprite(effect.getEffectType()), getX() + 5, (int) centerY + 3, 8, 8); // Сдвинул значок вниз на +4
                fontPotion.drawString(matrix, name + lvl, getX() + 20, centerY + 4, ColorUtil.getText());
                if (effect.getDuration() > 0 || effect.isInfinite()) {
                    fontPotion.drawString(matrix, duration, getX() + getWidth() - 6 - fontPotion.getStringWidth(duration), centerY + 4, ColorUtil.multRed(ColorUtil.getText(), animRed)); // Опустил время до конца зелий вниз
                }
            });

            int width = (int) fontPotion.getStringWidth(name + lvl + (effect.getDuration() > 0 || effect.isInfinite() ? duration : "")) + 32;
            maxWidth = Math.max(width, maxWidth);
            offset += (int) (14 * animation + spacing);
        }

        setWidth(maxWidth);
        setHeight(list.isEmpty() ? (int) 17.5F : offset);
    }

    private String getDuration(StatusEffectInstance pe) {
        int var1 = pe.getDuration();
        if (var1 <= 0 && !pe.isInfinite()) return "";
        int mins = var1 / 1200;
        return pe.isInfinite() || mins > 60 ? "**:**" : mins + ":" + String.format("%02d", (var1 % 1200) / 20);
    }

    private record Potion(StatusEffectInstance effect, Animation anim) {}
}
