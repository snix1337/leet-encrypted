package funny.leet.implement.events.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import funny.leet.api.event.events.Event;

@Getter
@AllArgsConstructor
public class RotationUpdateEvent implements Event {
    byte type;
}
