/*
package funny.leet.mixins;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import funny.leet.core.client.Constants;
import funny.leet.common.QuickImports;

import java.util.function.Supplier;

@Mixin(PlayerListEntry.class)
public class PlayerInfoMixin implements QuickImports {
    @Unique
    private final Identifier CAPE = Constants.getResource("textures/cape.png");

    @Inject(method = "texturesSupplier", at = @At("RETURN"), cancellable = true)
    private void texturesSupplier(GameProfile gameProfile, CallbackInfoReturnable<Supplier<SkinTextures>> cir) {
        try {
            PlayerListEntry self = (PlayerListEntry) (Object) this;
            if (mc.player != null && mc.player.getUuid().equals(gameProfile.getId())) {
                SkinTextures playerSkin = cir.getReturnValue().get();
                SkinTextures newPlayerSkin = new SkinTextures(
                        playerSkin.texture(),
                        playerSkin.textureUrl(),
                        CAPE,
                        playerSkin.elytraTexture(),
                        playerSkin.model(),
                        playerSkin.secure()
                );
                cir.setReturnValue(() -> newPlayerSkin);
            }
        } catch (Exception e) {
            System.err.println("Mixin error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
 */

// ADD "PlayerInfoMixin",