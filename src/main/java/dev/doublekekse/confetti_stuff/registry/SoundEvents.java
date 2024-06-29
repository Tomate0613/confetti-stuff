package dev.doublekekse.confetti_stuff.registry;

import dev.doublekekse.confetti_stuff.ConfettiStuff;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class SoundEvents {
    public static final SoundEvent CANNON_FIRES = SoundEvent.createFixedRangeEvent(ConfettiStuff.id("block.confetti_cannon.cannon_fires"), 16f);
    public static final SoundEvent POPPER_POPS = SoundEvent.createFixedRangeEvent(ConfettiStuff.id("item.popper.popper_pops"), 16f);
    public static final SoundEvent SWEEPING = SoundEvent.createFixedRangeEvent(ConfettiStuff.id("item.broom.sweeping"), 16f);

    public static void register() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, ConfettiStuff.id("block.confetti_cannon.cannon_fires"), CANNON_FIRES);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ConfettiStuff.id("item.popper.popper_pops"), POPPER_POPS);
        Registry.register(BuiltInRegistries.SOUND_EVENT, ConfettiStuff.id("item.broom.sweeping"), SWEEPING);
    }
}
