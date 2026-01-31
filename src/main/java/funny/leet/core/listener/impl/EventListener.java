package funny.leet.core.listener.impl;

import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import funny.leet.api.event.EventHandler;
import funny.leet.api.feature.draggable.AbstractDraggable;
import funny.leet.common.util.entity.PlayerInventoryComponent;
import funny.leet.common.util.world.ServerUtil;
import funny.leet.core.Main;
import funny.leet.core.listener.Listener;
import funny.leet.implement.events.item.UsingItemEvent;
import funny.leet.implement.events.packet.PacketEvent;
import funny.leet.implement.events.player.TickEvent;

public class EventListener implements Listener {
    public static boolean serverSprint;
    public static int selectedSlot;

    @EventHandler
    public void onTick(TickEvent e) {
        ServerUtil.tick();
        Main.getInstance().getAttackPerpetrator().tick();
        PlayerInventoryComponent.tick();
        Main.getInstance().getDraggableRepository().draggable().forEach(AbstractDraggable::tick);
    }

    @EventHandler
    public void onPacket(PacketEvent e) {
        switch (e.getPacket()) {
            case ClientCommandC2SPacket command -> serverSprint = switch (command.getMode()) {
                case ClientCommandC2SPacket.Mode.START_SPRINTING -> true;
                case ClientCommandC2SPacket.Mode.STOP_SPRINTING -> false;
                default -> serverSprint;
            };
            case UpdateSelectedSlotC2SPacket slot -> selectedSlot = slot.getSelectedSlot();
            default -> {}
        }
        ServerUtil.packet(e);
        Main.getInstance().getAttackPerpetrator().onPacket(e);
        Main.getInstance().getDraggableRepository().draggable().forEach(drag -> drag.packet(e));
    }

    @EventHandler
    public void onUsingItemEvent(UsingItemEvent e) {
        Main.getInstance().getAttackPerpetrator().onUsingItem(e);
    }
}
