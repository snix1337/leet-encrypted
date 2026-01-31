package funny.leet.implement.features.modules.combat;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.common.util.other.Instance;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoInteract extends Module {
    public static NoInteract getInstance() {
        return Instance.get(NoInteract.class);
    }

    public NoInteract() {
        super("NoInteract", "No Interact", ModuleCategory.COMBAT);
    }
}
