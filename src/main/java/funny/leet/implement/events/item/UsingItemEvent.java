package funny.leet.implement.events.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import funny.leet.api.event.events.callables.EventCancellable;

@Getter
@Setter
@AllArgsConstructor
public class UsingItemEvent extends EventCancellable {
    byte type;
}
