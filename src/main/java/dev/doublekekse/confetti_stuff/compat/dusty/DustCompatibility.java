package dev.doublekekse.confetti_stuff.compat.dusty;

import dev.mrturtle.other.DustUtil;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DustCompatibility {
    public static boolean enabled() {
        return FabricLoader.getInstance().isModLoaded("dust");
    }

    public static void onSweep(Level level, BlockPos blockPos) {
        if (!enabled()) {
            return;
        }

        if (level.isClientSide) {
            return;
        }

        DustUtil.modifyDustAt(level, blockPos, -0.5f);
    }
}
