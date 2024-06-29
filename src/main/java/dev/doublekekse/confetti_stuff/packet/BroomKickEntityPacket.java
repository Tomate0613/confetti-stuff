package dev.doublekekse.confetti_stuff.packet;

import dev.doublekekse.confetti_stuff.ConfettiStuff;
import dev.doublekekse.confetti_stuff.codec.Vec3StreamCodec;
import dev.doublekekse.confetti_stuff.item.Broom;
import dev.doublekekse.confetti_stuff.registry.SoundEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record BroomKickEntityPacket(int id, Vec3 velocity) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, BroomKickEntityPacket> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, BroomKickEntityPacket::id,
        Vec3StreamCodec.STREAM_CODEC, BroomKickEntityPacket::velocity,
        BroomKickEntityPacket::new
    );
    public static final CustomPacketPayload.Type<BroomKickEntityPacket> TYPE = new CustomPacketPayload.Type<>(ConfettiStuff.id("broom_kick_entity_packet"));

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(BroomKickEntityPacket packet, ClientPlayNetworking.Context context) {
        var player = context.player();

        player.addDeltaMovement(packet.velocity);
        player.playSound(SoundEvents.SWEEPING, 1, 1.5f);
    }

    public static void handleServer(BroomKickEntityPacket packet, ServerPlayNetworking.Context context) {
        if(packet.velocity.lengthSqr() > 5) {
            return;
        }

        var player = context.player();
        var entity = player.level().getEntity(packet.id);

        if(entity == null) {
            return;
        }

        var maxDistance = player.blockInteractionRange() / Broom.BROOM_INTERACTION_DISTANCE_FACTOR;
        var maxDistanceSqr = maxDistance * maxDistance + 25;

        if(entity.distanceToSqr(player.position()) > maxDistanceSqr) {
            return;
        }

        if(entity instanceof ServerPlayer serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, packet);
        }

        entity.addDeltaMovement(packet.velocity);
    }
}
