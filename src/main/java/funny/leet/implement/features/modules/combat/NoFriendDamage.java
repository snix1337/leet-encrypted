package funny.leet.implement.features.modules.combat;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import funny.leet.api.repository.friend.FriendUtils;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.event.EventHandler;
import funny.leet.implement.events.player.AttackEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NoFriendDamage extends Module {
    public NoFriendDamage() {
        super("NoFriendDamage", "No Friend Damage", ModuleCategory.COMBAT);
    }

    @EventHandler
    public void onAttack(AttackEvent e) {
        e.setCancelled(FriendUtils.isFriend(e.getEntity()));
    }
}

