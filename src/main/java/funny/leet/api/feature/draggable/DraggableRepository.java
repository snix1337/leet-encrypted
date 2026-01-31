package funny.leet.api.feature.draggable;

import funny.leet.implement.features.draggables.*;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import obf.uscate.annotations.Compile;
import obf.uscate.annotations.Initialization;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DraggableRepository {
    List<AbstractDraggable> draggable = new ArrayList<>();

    @Compile
    @Initialization
    public void setup() {
        register(
                new TargetHud(),
                new Potions(),
                new HotKeys(),
                new BossBars(),
                new Armor(),
                new Watermark(),
                new Inventory(),
                new CoolDowns(),
                new StaffList(),
                new Notifications(),
                new ScoreBoard(),
                new MediaPlayer(),
                new HotBar(),
                new PlayerInfo()
        );
    }

    @Compile
    @Initialization
    public void register(AbstractDraggable... module) {
        draggable.addAll(List.of(module));
    }

    public List<AbstractDraggable> draggable() {
        return draggable;
    }

    public <T extends AbstractDraggable> T get(String name) {
        return draggable.stream()
                .filter(module -> module.getName().equalsIgnoreCase(name))
                .map(module -> (T) module)
                .findFirst()
                .orElse(null);
    }

    public <T extends AbstractDraggable> T get(Class<T> clazz) {
        return draggable.stream()
                .filter(module -> clazz.isAssignableFrom(module.getClass()))
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }
}
