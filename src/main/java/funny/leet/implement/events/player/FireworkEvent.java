package funny.leet.implement.events.player;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import net.minecraft.util.math.Vec3d;
import funny.leet.api.event.events.Event;

@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FireworkEvent implements Event {
    public Vec3d vector;

}
