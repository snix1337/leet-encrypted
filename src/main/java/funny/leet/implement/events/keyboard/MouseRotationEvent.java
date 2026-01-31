package funny.leet.implement.events.keyboard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import funny.leet.api.event.events.callables.EventCancellable;

@Getter
@Setter
@AllArgsConstructor
public class MouseRotationEvent extends EventCancellable {
    float cursorDeltaX, cursorDeltaY;
}
