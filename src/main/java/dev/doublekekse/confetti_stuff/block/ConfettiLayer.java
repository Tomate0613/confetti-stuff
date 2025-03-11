package dev.doublekekse.confetti_stuff.block;

import dev.doublekekse.confetti_stuff.ConfettiStuff;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
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
import org.joml.Vector2i;

public class ConfettiLayer extends CarpetBlock {
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 1, 4);
    protected static final VoxelShape COLLISION_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 0.0, 16.0);
    private static final Vector2i[] CHECK_DIRS = {
        new Vector2i(-1, -1),
        new Vector2i(-1, 0),
        new Vector2i(-1, 1),
        new Vector2i(0, 1),
        new Vector2i(1, 1),
        new Vector2i(1, 0),
        new Vector2i(1, -1),
        new Vector2i(0, -1),
    };

    public ConfettiLayer(Properties properties) {
        super(properties);
    }

    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        var variant = (int) Math.floor(Math.random() * (canPlaceLarge(blockPlaceContext) ? 4 : 3)) + 1;
        return this.defaultBlockState().setValue(VARIANT, variant);
    }

    private boolean canPlaceLarge(BlockPlaceContext blockPlaceContext) {
        var level = blockPlaceContext.getLevel();
        var pos = blockPlaceContext.getClickedPos();

        for (var dir : CHECK_DIRS) {
            var checkPos = pos.offset(dir.x, 0, dir.y);
            var state = level.getBlockState(checkPos);

            if (state.is(ConfettiStuff.CONFETTI_LAYER) && state.getValue(VARIANT) == 4) {
                return false;
            }
        }

        return true;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(VARIANT);
    }

    @Override
    protected @NotNull VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return COLLISION_SHAPE;
    }
}
