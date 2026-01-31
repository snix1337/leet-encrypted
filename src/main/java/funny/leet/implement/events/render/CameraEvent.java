package funny.leet.implement.events.render;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import funny.leet.api.event.events.callables.EventCancellable;
import funny.leet.implement.features.modules.combat.killaura.rotation.Angle;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CameraEvent extends EventCancellable {
    boolean cameraClip;
    float distance;
    Angle angle;
}
