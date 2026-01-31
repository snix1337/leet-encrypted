package funny.leet.implement.events.player;

import lombok.*;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.Entity;
import funny.leet.api.event.events.callables.EventCancellable;

@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttackEvent extends EventCancellable {
    Entity entity;
}
