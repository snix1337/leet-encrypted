package funny.leet.implement.features.draggables;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
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
import funny.leet.common.util.other.Instance;
import funny.leet.common.util.render.Render2DUtil;
import funny.leet.implement.features.modules.render.Hud;

import java.util.*;

public class StaffList extends AbstractDraggable {
    public static StaffList getInstance() {
        return Instance.getDraggable(StaffList.class);
    }

    public final Map<PlayerListEntry, Animation> list = new HashMap<>();
    private final List<String> staffPrefix = List.of("helper", "moder", "staff", "admin", "curator", "стажёр", "staff", "сотрудник", "помощник", "админ", "модер");

    public StaffList() {
        super("Staff List", 130, 40, 80, 23, true);
    }

    @Override
    public boolean visible() {
        return !list.isEmpty() || PlayerIntersectionUtil.isChat(mc.currentScreen);
    }

    @Override
    public void tick() {
        Collection<PlayerListEntry> playerList = Objects.requireNonNull(mc.player).networkHandler.getPlayerList();
        for (PlayerListEntry entry : playerList) {
            GameProfile profile = entry.getProfile();
            Text displayName = entry.getDisplayName();
            if (displayName == null || profile == null) continue;
            String prefix = displayName.getString().replace(profile.getName(), "");
            if (prefix.length() < 2) continue;

            PlayerListEntry player = new PlayerListEntry(profile, false);
            player.setDisplayName(displayName);

            if (list.keySet().stream().noneMatch(p -> Objects.equals(p.getDisplayName(), player.getDisplayName()))) {
                staffPrefix.stream().filter(s -> prefix.toLowerCase().contains(s)).findFirst().ifPresent(s -> {
                    list.put(player, new DecelerateAnimation().setMs(150).setValue(1));
                    if (Hud.getInstance().notificationSettings.isSelected("Staff Join")) {
                        Notifications.getInstance().addList(Text.empty().append(player.getDisplayName()).append(" - Зашел на сервер!"), 2000);
                    }
                });
            }
        }
        list.entrySet().stream().filter(s -> playerList.stream().noneMatch(p -> Objects.equals(s.getKey().getDisplayName(), p.getDisplayName()))).forEach(s -> s.getValue().setDirection(Direction.BACKWARDS));
        list.values().removeIf(s -> s.isFinished(Direction.BACKWARDS));
        super.tick();
    }

    @Override
    public void drawDraggable(DrawContext context) {
        MatrixStack matrix = context.getMatrices();

        FontRenderer font = Fonts.getSize(19, Fonts.Type.BOLD);
        FontRenderer fontPlayer = Fonts.getSize(17, Fonts.Type.DEFAULT);
        float spacing = 1.0F;

        float blurSoftness = 1.0F + list.size() * 0.2F;

        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth(), 17.5F).round(6,6,6,6).softness(blurSoftness).thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F)).color(ColorUtil.multAlpha(ColorUtil.BLACK, 0.65f)).build());
        blur.render(ShapeProperties.create(matrix, getX(), getY(), getWidth(), 17.5F).round(6,6,6,6).softness(blurSoftness).thickness(2)
                .outlineColor(ColorUtil.getOutline(0.5F)).color(ColorUtil.multAlpha(ColorUtil.getClientColor(), 0.07f)).build());

        float centerX = getX() + getWidth() / 2.0F;
        font.drawString(matrix, getName(), (int) (centerX - font.getStringWidth(getName()) / 2.0F), getY() + 5.7F, ColorUtil.getText()); // Поднял текст "Staff List" вверх

        int offset = 23;
        int maxWidth = 80;

        for (Map.Entry<PlayerListEntry, Animation> staff : list.entrySet()) {
            PlayerListEntry player = staff.getKey();

            if (player == null) continue;

            Text text = player.getDisplayName();
            float centerY = getY() + offset;
            float width = fontPlayer.getStringWidth(text) + 27;
            float animation = staff.getValue().getOutput().floatValue();

            blur.render(ShapeProperties.create(matrix, getX(), getY() + offset, getWidth(), 14)
                    .round(5,5,5,5)
                    .softness(blurSoftness)
                    .thickness(2)
                    .outlineColor(ColorUtil.getOutline(0.5F))
                    .color(ColorUtil.getRect(0.7F))
                    .build());

            MathUtil.scale(matrix, centerX, centerY, 1, animation, () -> {
                Render2DUtil.drawTexture(context, player.getSkinTextures().texture(), getX() + 5, (int) centerY + 4, 7, 3.5F, 8, 8, 64, ColorUtil.getRect(1)); // Сдвинул аватарку вниз на +4
                Render2DUtil.drawRect(matrix, getX() + 16, centerY - 1, 0.5F, 8, ColorUtil.getOutline(1, 0.5F));
                fontPlayer.drawText(matrix, text, getX() + 22, centerY + 4);
            });

            offset += (int) (14 * animation + spacing);
            maxWidth = (int) Math.max(width, maxWidth);
        }

        setWidth(maxWidth);
        setHeight(offset);
    }
}
