package dev.doublekekse.confetti_stuff.compat.dusty;

import dev.mrturtle.other.DustUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class DustCompatibility {
    public static void onSweep(Level level, BlockPos blockPos) {
        if (level.isClientSide) {
            return;
        }

        DustUtil.modifyDustAt(level, blockPos, -0.5f);
    }
}
