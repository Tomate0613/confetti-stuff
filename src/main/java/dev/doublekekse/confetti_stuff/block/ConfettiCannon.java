package dev.doublekekse.confetti_stuff.block;


import dev.doublekekse.confetti.Confetti;
import dev.doublekekse.confetti.math.Vec3Dist;
import dev.doublekekse.confetti.packet.ExtendedParticlePacket;
import dev.doublekekse.confetti_stuff.registry.SoundEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ConfettiCannon extends Block {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty TRIGGERED = BlockStateProperties.TRIGGERED;

    public ConfettiCannon(Properties properties) {
        super(properties);

        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TRIGGERED, false));
    }


    protected void neighborChanged(BlockState blockState, Level level, BlockPos blockPos, Block block, BlockPos blockPos2, boolean bl) {
        boolean powered = level.hasNeighborSignal(blockPos);
        boolean triggered = blockState.getValue(TRIGGERED);

        if (powered && !triggered) {
            level.scheduleTick(blockPos, this, 4);
            level.setBlock(blockPos, blockState.setValue(TRIGGERED, true), 2);
        } else if (!powered && triggered) {
            level.setBlock(blockPos, blockState.setValue(TRIGGERED, false), 2);
        }
    }


    protected void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, RandomSource randomSource) {
        var forward = blockState.getValue(FACING).getNormal();
        var pos = new Vec3(forward.getX(), 0.3, forward.getZ()).add(blockPos.getCenter());
        var dir = new Vec3(forward.getX() * 1.2, 1, forward.getZ() * 1.2);

        serverLevel.players().forEach(player -> {
            ServerPlayNetworking.send(player, new ExtendedParticlePacket(new Vec3Dist(pos, 0.05), new Vec3Dist(dir, .2), 500, false, Confetti.CONFETTI));
        });

        serverLevel.playSound(null, pos.x, pos.y, pos.z, SoundEvents.CANNON_FIRES, SoundSource.BLOCKS);
    }


    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NotNull VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return Block.box(2, 0, 2, 14, 14, 14);
    }

    protected @NotNull BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    protected @NotNull BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.rotate(mirror.getRotation(blockState.getValue(FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TRIGGERED);
    }

    protected boolean isSignalSource(BlockState blockState) {
        return true;
    }
}
