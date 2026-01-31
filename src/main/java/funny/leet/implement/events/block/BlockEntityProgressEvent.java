package funny.leet.implement.events.block;

import net.minecraft.block.entity.BlockEntity;
import funny.leet.api.event.events.Event;

public record BlockEntityProgressEvent(BlockEntity blockEntity, Type type) implements Event {
    public enum Type {
        ADD, REMOVE
    }
}
