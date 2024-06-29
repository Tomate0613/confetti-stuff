package dev.doublekekse.confetti_stuff.codec;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class Vec3StreamCodec implements StreamCodec<FriendlyByteBuf, Vec3> {
    public static final Vec3StreamCodec STREAM_CODEC = new Vec3StreamCodec();

    @Override
    public @NotNull Vec3 decode(FriendlyByteBuf byteBuf) {
        return byteBuf.readVec3();
    }

    @Override
    public void encode(FriendlyByteBuf byteBuf, Vec3 vec3) {
        byteBuf.writeVec3(vec3);
    }
}
