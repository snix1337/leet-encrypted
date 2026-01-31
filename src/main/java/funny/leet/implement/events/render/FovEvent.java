package funny.leet.implement.events.render;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import funny.leet.api.event.events.callables.EventCancellable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FovEvent extends EventCancellable {
    int fov;
}
