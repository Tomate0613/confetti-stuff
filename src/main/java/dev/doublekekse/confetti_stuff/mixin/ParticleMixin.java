package dev.doublekekse.confetti_stuff.mixin;

import dev.doublekekse.confetti.particle.ConfettiParticle;
import dev.doublekekse.confetti_stuff.ConfettiStuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConfettiParticle.ConfettiPieceParticle.class)
public abstract class ParticleMixin extends TextureSheetParticle {
    @Shadow
    abstract void collision(double dX, double dY, double dZ, double radius, double height, Vec3 dir);

    protected ParticleMixin(ClientLevel clientLevel, double d, double e, double f) {
        super(clientLevel, d, e, f);
    }

    @Inject(method = "collisions", at = @At(value = "INVOKE", target = "Ldev/doublekekse/confetti/particle/ConfettiParticle$ConfettiPieceParticle;collision(DDDDDLnet/minecraft/world/phys/Vec3;)V", ordinal = 0))
    void tick(CallbackInfo ci) {
        var player = Minecraft.getInstance().player;
        assert player != null;

        if (!player.getUseItem().is(ConfettiStuff.BROOM)) {
            return;
        }

        var hitResult = Minecraft.getInstance().hitResult;

        if (hitResult == null || ConfettiStuff.BROOM.viewDelta == null) {
            return;
        }

        var pos = hitResult.getLocation();

        var delta = ConfettiStuff.BROOM.viewDelta.with(Direction.Axis.Y, 0);
        if (delta.lengthSqr() > .5) {
            delta = delta.normalize().scale(.5);
        }

        double dX = x - pos.x;
        double dY = y - pos.y;
        double dZ = z - pos.z;

        collision(dX, dY, dZ, 1, 0.1, delta.scale(.6));
    }
}
