package dev.doublekekse.confetti_stuff.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class ConfettiLayer extends CarpetBlock {
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 1, 4);
    protected static final VoxelShape COLLISION_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 0.0, 16.0);

    public ConfettiLayer(Properties properties) {
        super(properties);
    }

    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return this.defaultBlockState().setValue(VARIANT, (int) Math.floor(Math.random() * 4) + 1);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return COLLISION_SHAPE;
    }
}
