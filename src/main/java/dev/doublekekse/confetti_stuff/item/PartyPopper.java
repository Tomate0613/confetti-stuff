package dev.doublekekse.confetti_stuff.item;

import dev.doublekekse.confetti.Confetti;
import dev.doublekekse.confetti.math.Vec3Dist;
import dev.doublekekse.confetti.packet.ExtendedParticlePacket;
import dev.doublekekse.confetti_stuff.registry.SoundEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class PartyPopper extends Item {
    public static final DispenseItemBehavior DISPENSE_ITEM_BEHAVIOR = new DefaultDispenseItemBehavior() {
        protected @NotNull ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
            dispensePartyPopper(blockSource, itemStack);
            return itemStack;
        }
    };

    public PartyPopper(Properties properties) {
        super(properties);

        DispenserBlock.registerBehavior(this, DISPENSE_ITEM_BEHAVIOR);
    }

    public static void dispensePartyPopper(BlockSource blockSource, ItemStack itemStack) {
        BlockPos forwardPos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
        var dir = forwardPos.getCenter().subtract(blockSource.center());

        blockSource.level().players().forEach(serverPlayer -> {
            ServerPlayNetworking.send(serverPlayer, new ExtendedParticlePacket(new Vec3Dist(blockSource.center().add(dir.scale(.5).add(0, -.3, 0)), 0), new Vec3Dist(dir.scale(.4), new Vec3(.1, .15, .1)), 60, false, Confetti.CONFETTI));
        });

        itemStack.shrink(1);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        player.playSound(SoundEvents.POPPER_POPS, .3f, 1f);
        var forward = player.getLookAngle().normalize();

        if (!level.isClientSide) {
            itemStack.consume(1, player);
            player.awardStat(Stats.ITEM_USED.get(this));

            var right = forward.cross(new Vec3(0, 1.7, 0));

            var pos = player.position()
                .add(0, 1, 0)
                .add(right.scale(.3))
                .add(forward.scale(.5));

            level.players().forEach(serverPlayer -> {
                ServerPlayNetworking.send((ServerPlayer) serverPlayer, new ExtendedParticlePacket(new Vec3Dist(pos, 0), new Vec3Dist(forward.scale(.4), new Vec3(.1, .15, .1)), 60, false, Confetti.CONFETTI));
            });
        }


        if (level.isClientSide) {
            if (!player.isShiftKeyDown()) {
                player.addDeltaMovement(forward.scale(-1));
            }
        }

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(interactionHand), level.isClientSide());
    }
}
