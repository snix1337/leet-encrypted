package funny.leet.implement.features.draggables;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import funny.leet.api.feature.draggable.AbstractDraggable;
import funny.leet.api.system.animation.Animation;
import funny.leet.api.system.animation.Direction;
import funny.leet.api.system.animation.implement.DecelerateAnimation;
import funny.leet.api.system.font.FontRenderer;
import funny.leet.api.system.font.Fonts;
import funny.leet.api.system.shape.ShapeProperties;
import funny.leet.api.system.sound.SoundManager;
import funny.leet.common.util.color.ColorUtil;
import funny.leet.common.util.math.MathUtil;
import funny.leet.common.util.entity.PlayerIntersectionUtil;
import funny.leet.common.util.other.Instance;
import funny.leet.implement.events.container.SetScreenEvent;
import funny.leet.implement.events.packet.PacketEvent;
import funny.leet.implement.features.modules.render.Hud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class Notifications extends AbstractDraggable {

    public static Notifications getInstance() {
        return Instance.getDraggable(Notifications.class);
    }

    private final List<Notification> list = new ArrayList<>();
    private final List<Stack> stacks = new ArrayList<>();

    public Notifications() {
        super("Notifications", 0, 50, 100, 15, true);
    }

    @Override
    public void tick() {
        list.forEach(notif -> {
            if (System.currentTimeMillis() > notif.removeTime ||
                    (notif.text.getString().contains("Notification Example") && !PlayerIntersectionUtil.isChat(mc.currentScreen)))
                notif.anim.setDirection(Direction.BACKWARDS);
        });
        list.removeIf(notif -> notif.anim.isFinished(Direction.BACKWARDS));

        while (!stacks.isEmpty()) {
            addTextIfNotEmpty(TypePickUp.INVENTORY, "Objects have been lifted: ");
            addTextIfNotEmpty(TypePickUp.SHULKER_INVENTORY, "Items are stacked in a shulker: ");
            addTextIfNotEmpty(TypePickUp.SHULKER, "The shulker has been lifted with: ");
        }
    }

    @Override
    public void packet(PacketEvent e) {
        if (!PlayerIntersectionUtil.nullCheck()) switch (e.getPacket()) {
            case ItemPickupAnimationS2CPacket item when Hud.getInstance().notificationSettings.isSelected("Item Pick Up")
                    && item.getCollectorEntityId() == Objects.requireNonNull(mc.player).getId()
                    && Objects.requireNonNull(mc.world).getEntityById(item.getEntityId()) instanceof ItemEntity entity -> {

                ItemStack itemStack = entity.getStack();
                ContainerComponent component = itemStack.get(DataComponentTypes.CONTAINER);

                if (component == null) {
                    Text itemText = itemStack.getName();
                    if (itemText.getContent().toString().equals("empty")) {
                        MutableText text = Text.empty().append(itemText);
                        if (itemStack.getCount() > 1)
                            text.append(Formatting.RESET + " [" + Formatting.RED + itemStack.getCount() + Formatting.GRAY + "x" + Formatting.RESET + "]");
                        stacks.add(new Stack(TypePickUp.INVENTORY, text));
                    }
                } else {
                    component.stream()
                            .filter(s -> s.getName().getContent().toString().equals("empty"))
                            .forEach(stack -> {
                                MutableText text = Text.empty().append(stack.getName());
                                if (stack.getCount() > 1)
                                    text.append(Formatting.RESET + " [" + Formatting.RED + stack.getCount() + Formatting.GRAY + "x" + Formatting.RESET + "]");
                                stacks.add(new Stack(TypePickUp.SHULKER, text));
                            });
                }
            }
            case ScreenHandlerSlotUpdateS2CPacket slot when Hud.getInstance().notificationSettings.isSelected("Item Pick Up") -> {
                int slotId = slot.getSlot();
                ContainerComponent updatedContainer = slot.getStack().get(DataComponentTypes.CONTAINER);
                if (updatedContainer != null && slotId < Objects.requireNonNull(mc.player).currentScreenHandler.slots.size() && slot.getSyncId() == 0) {
                    ContainerComponent currentContainer = mc.player.currentScreenHandler.getSlot(slotId).getStack().get(DataComponentTypes.CONTAINER);
                    if (currentContainer != null) {
                        updatedContainer.stream()
                                .filter(stack -> currentContainer.stream().noneMatch(s ->
                                        Objects.equals(s.getComponents(), stack.getComponents()) && s.toString().equals(stack.toString())))
                                .forEach(stack -> {
                                    MutableText text = Text.empty().append(stack.getName());
                                    stacks.add(new Stack(TypePickUp.SHULKER_INVENTORY, text));
                                });
                    }
                }
            }
            default -> {}
        }
    }

    @Override
    public void setScreen(SetScreenEvent e) {
        if (e.getScreen() instanceof ChatScreen) {
            addList("Notification Example", 99999999);
        }
    }

    @Override
    public void drawDraggable(DrawContext context) {
        MatrixStack matrix = context.getMatrices();
        FontRenderer font = Fonts.getSize(12, Fonts.Type.DEFAULT);
        setX((window.getScaledWidth() - getWidth()) / 2);

        float offsetY = 0;
        float offsetX = 5;

        for (Notification notification : list) {
            float anim = notification.anim.getOutput().floatValue();
            float width = font.getStringWidth(notification.text) + offsetX * 2;
            float startY = getY() + offsetY;
            float startX = getX() + (getWidth() - width) / 2;

            MathUtil.setAlpha(anim, () -> {
                blur.render(ShapeProperties.create(matrix, startX, startY, width, getHeight())
                        .round(4).softness(1).thickness(2)
                        .outlineColor(ColorUtil.getOutline(0.5F))
                        .color(ColorUtil.getRect(0.65F))
                        .build());
                font.drawText(matrix, notification.text, (int)(startX + offsetX), startY + 6.5F);
            });

            offsetY += (getHeight() + 3) * anim;
        }
    }

    private void addTextIfNotEmpty(TypePickUp type, String prefix) {
        MutableText text = Text.empty();
        List<Stack> filtered = stacks.stream().filter(stack -> stack.type.equals(type)).toList();

        for (int i = 0, size = filtered.size(); i < size; i++) {
            Stack stack = filtered.get(i);
            if (stack.type != type) continue;
            text.append(stack.text);
            stacks.remove(stack);
            if (text.getString().length() > 150) break;
            if (i + 1 != size) text.append(" , ");
        }

        if (!text.equals(Text.empty())) {
            addList(Text.empty().append(prefix).append(text), 2000);
        }
    }

    public void addList(String text, long removeTime) {
        addList(text, removeTime, null);
    }

    public void addList(Text text, long removeTime) {
        addList(text, removeTime, null);
    }

    public void addList(String text, long removeTime, SoundEvent sound) {
        addList(Text.empty().append(text), removeTime, sound);
    }

    public void addList(Text text, long removeTime, SoundEvent sound) {
        list.add(new Notification(text, new DecelerateAnimation().setMs(300).setValue(1), System.currentTimeMillis() + removeTime));
        if (list.size() > 12) list.removeFirst();
        list.sort(Comparator.comparingDouble(notif -> -notif.removeTime));
        if (sound != null) SoundManager.playSound(sound);
    }

    public record Notification(Text text, Animation anim, long removeTime) {}
    public record Stack(TypePickUp type, MutableText text) {}
    public enum TypePickUp { INVENTORY, SHULKER, SHULKER_INVENTORY }
}
