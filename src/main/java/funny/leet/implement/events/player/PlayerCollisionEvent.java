package funny.leet.implement.events.player;

import lombok.*;
import lombok.experimental.FieldDefaults;
import net.minecraft.block.Block;
import funny.leet.api.event.events.callables.EventCancellable;

@Setter
@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerCollisionEvent extends EventCancellable {
    private Block block;

}
