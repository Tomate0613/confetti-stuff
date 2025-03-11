package dev.doublekekse.confetti_stuff.item;

import dev.doublekekse.confetti_stuff.ConfettiStuff;
import dev.doublekekse.confetti_stuff.compat.dusty.DustCompatibility;
import dev.doublekekse.confetti_stuff.packet.BroomKickEntityPacket;
import dev.doublekekse.confetti_stuff.registry.SoundEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

import java.util.function.Predicate;

public class Broom extends Item {
    public static final double BROOM_INTERACTION_DISTANCE_FACTOR = 1.7;

    public Broom(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);

        player.startUsingItem(interactionHand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int i) {
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        var random = level.getRandom();
        var cooldowns = player.getCooldowns();
        var movement = player.getDeltaMovement();
        var horizontalMovement = new Vec3(movement.x, 0, movement.z);
        var speedSqr = horizontalMovement.lengthSqr();

        HitResult hitResult = calculateHitResult(player);

        BLOCK:
        if (hitResult instanceof BlockHitResult blockHitResult) {
            var blockPos = blockHitResult.getBlockPos();
            var blockState = level.getBlockState(blockPos);

            if (blockState.is(Blocks.AIR)) {
                break BLOCK;
            }

            if (i % 3 == 0) {
                player.playSound(SoundEvents.SWEEPING, (float) ((random.nextFloat() + .5) * speedSqr * 50), random.nextFloat());
            }

            if (blockState.is(ConfettiStuff.CONFETTI_LAYER) && !level.isClientSide) {
                var serverPlayer = (ServerPlayer) player;
                serverPlayer.gameMode.destroyBlock(blockPos);
            }

            if (level.isClientSide) {
                spawnDustParticles(level, blockHitResult, blockState, speedSqr);
            }

            if(FabricLoader.getInstance().isModLoaded("dust")) {
                DustCompatibility.onSweep(level, blockPos);
            }
        } else if (hitResult instanceof EntityHitResult entityHitResult && !cooldowns.isOnCooldown(this) && level.isClientSide) {
            var entity = entityHitResult.getEntity();
            Vec3 viewVector = player.getViewVector(0.0f);
            var horizontalViewVec = new Vec3(viewVector.x, 0, viewVector.z).normalize();

            if (entity.getY() > player.getY()) {
                kickEntity(entity, horizontalViewVec.add(0, 1.2, 0));
            } else {
                kickEntity(entity, horizontalViewVec.add(0, .5, 0));
            }

            player.playSound(SoundEvents.SWEEPING, 1, 1.5f);

            cooldowns.addCooldown(this, 15);
        }
    }

    private void kickEntity(Entity entity, Vec3 velocity) {
        ClientPlayNetworking.send(new BroomKickEntityPacket(entity.getId(), velocity));
    }

    private void spawnDustParticles(Level level, BlockHitResult blockHitResult, BlockState blockState, double speed) {
        var random = level.getRandom();
        var particleCount = Math.min(random.nextInt(2, 7) * speed * 50, 20);
        var blockParticleOption = new BlockParticleOption(ParticleTypes.BLOCK, blockState);
        var pos = blockHitResult.getLocation();

        for (int i = 0; i < particleCount; ++i) {
            level.addParticle(blockParticleOption, false, pos.x + random.nextDouble() - .5, pos.y, pos.z + random.nextDouble() - .5, 0, 0, 0);
        }
    }

    private HitResult calculateHitResult(Player player) {
        Vec3 viewVector = player.getViewVector(0.0f).scale(player.blockInteractionRange() / BROOM_INTERACTION_DISTANCE_FACTOR);
        Level level = player.level();
        Vec3 eyePosition = player.getEyePosition();

        return getHitResult(eyePosition, player, entity -> !entity.isSpectator() && entity.isPickable(), viewVector, level);
    }


    private static HitResult getHitResult(Vec3 eyePosition, Entity entity, Predicate<Entity> predicate, Vec3 viewVector, Level level) {
        Vec3 endPosition = eyePosition.add(viewVector);

        HitResult hitResult = level.clip(new ClipContext(eyePosition, endPosition, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, entity));
        if (hitResult.getType() != HitResult.Type.MISS) {
            endPosition = hitResult.getLocation();
        }

        EntityHitResult hitResult2 = ProjectileUtil.getEntityHitResult(level, entity, eyePosition, endPosition, entity.getBoundingBox().expandTowards(viewVector).inflate(1.0), predicate, (float) 0.0);
        if (hitResult2 != null) {
            hitResult = hitResult2;
        }

        return hitResult;
    }
}
