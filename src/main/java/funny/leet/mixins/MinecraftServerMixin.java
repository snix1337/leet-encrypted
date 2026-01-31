package funny.leet.mixins;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import funny.leet.core.Main;
import funny.leet.api.file.exception.FileProcessingException;
import funny.leet.common.util.logger.LoggerUtil;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(method = "shutdown", at = @At("HEAD"))
    public void shutdown(CallbackInfo ci) {
        if (Main.getInstance().isInitialized()) {
            try {
                Main.getInstance().getFileController().saveFiles();
            } catch (FileProcessingException e) {
                LoggerUtil.error("Error occurred while saving files: " + e.getMessage());
            }
        }
    }
}
