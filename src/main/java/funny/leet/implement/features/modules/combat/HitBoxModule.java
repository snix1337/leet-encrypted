package funny.leet.implement.features.modules.combat;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import funny.leet.api.repository.friend.FriendUtils;
import funny.leet.api.feature.module.Module;
import funny.leet.api.feature.module.ModuleCategory;
import funny.leet.api.feature.module.setting.implement.ValueSetting;
import funny.leet.api.event.EventHandler;
import funny.leet.implement.events.player.BoundingBoxControlEvent;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HitBoxModule extends Module {
    ValueSetting xzExpandSetting = new ValueSetting("XZ Expand", "Allows the box to be extended in the XZ axis")
            .setValue(0.2F).range(0.0F, 3.0F);

    ValueSetting yExpandSetting = new ValueSetting("Y Expand", "Allows the box to be extended in the Y axis")
            .setValue(0.0F)
            .range(0.0F, 3.0F);

    public HitBoxModule() {
        super("Hit Box", ModuleCategory.COMBAT);
        setup(xzExpandSetting, yExpandSetting);
    }

    @EventHandler
    public void onBoundingBoxControl(BoundingBoxControlEvent event) {
        if (event.getEntity() instanceof LivingEntity living) {
            Box box = event.getBox();

            float xzExpand = xzExpandSetting.getValue();
            float yExpand = yExpandSetting.getValue();

            if (living != mc.player && !FriendUtils.isFriend(living)) {
                Box changedBox = new Box(
                        box.minX - xzExpand / 2.0f,
                        box.minY - yExpand / 2.0f,
                        box.minZ - xzExpand / 2.0f,
                        box.maxX + xzExpand / 2.0f,
                        Math.min(box.maxY + yExpand / 2.0f, living.getEyeY() + 0.5f), // Limit upward expansion to avoid floating
                        box.maxZ + xzExpand / 2.0f
                );

                double groundY = living.getBlockY();
                if (changedBox.minY < groundY) {
                    changedBox = new Box(
                            changedBox.minX,
                            groundY,
                            changedBox.minZ,
                            changedBox.maxX,
                            changedBox.maxY,
                            changedBox.maxZ
                    );
                }

                event.setBox(changedBox);
            }
        }
    }
}
