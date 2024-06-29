package dev.doublekekse.confetti_stuff.mixin;

import com.mojang.authlib.GameProfile;
import dev.doublekekse.confetti_stuff.ConfettiStuff;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends AbstractClientPlayer {
    @Shadow private @Nullable InteractionHand usingItemHand;

    public LocalPlayerMixin(ClientLevel clientLevel, GameProfile gameProfile) {
        super(clientLevel, gameProfile);
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z"))
    boolean isUsingItem(LocalPlayer instance) {
        if(usingItemHand != null) {
            var item = getItemInHand(usingItemHand);

            if(item.is(ConfettiStuff.BROOM)) {
                return false;
            }
        }

        return instance.isUsingItem();
    }
}
