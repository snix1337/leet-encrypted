package funny.leet.mixins;

import funny.leet.implement.features.modules.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import funny.leet.common.QuickImports;

@Mixin(ClientWorld.Properties.class)
public class ClientWorldPropertiesMixin implements QuickImports {

    @Shadow private long timeOfDay;

    @Inject(method = "setTimeOfDay", at = @At("HEAD"), cancellable = true)
    public void setTimeOfDayHook(long timeOfDay, CallbackInfo ci) {
        WorldRenderer tweaks = WorldRenderer.getInstance();
        if (tweaks.state && tweaks.modeSetting.isSelected("Time")) {
            this.timeOfDay = tweaks.timeSetting.getInt() * 1000L;
            ci.cancel();
        }
    }
}
