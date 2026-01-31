package funny.leet.implement.events.chat;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import funny.leet.api.event.events.callables.EventCancellable;

@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatEvent extends EventCancellable {
    String message;
}
