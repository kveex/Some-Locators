package me.kveex.somelocators.block;

import com.mojang.serialization.MapCodec;
import commonnetwork.networking.data.PacketContext;
import me.kveex.somelocators.Constants;
import me.kveex.somelocators.block.entity.LocatorInspectorBlockEntity;
import me.kveex.somelocators.block.entity.util.InsertResult;
import me.kveex.somelocators.block.properties.LocatorInspectorHalf;
import me.kveex.somelocators.network.locatorinspector.SetUnusedPayload;
import me.kveex.somelocators.network.locatorinspector.WriteLodestoneComponentPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class LocatorInspectorBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final EnumProperty<LocatorInspectorHalf> HALF = EnumProperty.create("half", LocatorInspectorHalf.class);
    private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateHorizontal(Block.boxZ(16.0, 8.0, 16.0));

    public LocatorInspectorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(HALF, LocatorInspectorHalf.BOTTOM)
                        .setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    protected @NonNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(LocatorInspectorBlock::new);
    }

    @Override
    protected @NonNull VoxelShape getShape(BlockState state, @NonNull BlockGetter level,
                                           @NonNull BlockPos pos, @NonNull CollisionContext context) {
        Direction facing = state.getValue(FACING);
        return SHAPES.get(facing);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, @NonNull Level level,
                                                        @NonNull BlockPos pos, @NonNull Player player,
                                                        @NonNull BlockHitResult hitResult) {
        InteractionHand mainHand = InteractionHand.MAIN_HAND;
        ItemStack stack = player.getItemInHand(mainHand);
        LocatorInspectorHalf half = state.getValue(HALF);
        BlockPos blockEntityPos = half == LocatorInspectorHalf.TOP ? pos.below() : pos;

        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
        if (!(level.getBlockEntity(blockEntityPos) instanceof LocatorInspectorBlockEntity blockEntity)) return InteractionResult.PASS;
        if (blockEntity.isCurrentlyUsed()) return InteractionResult.FAIL;

        if (half == LocatorInspectorHalf.TOP) {
            if (blockEntity.isEmpty()) {
                player.displayClientMessage(Component.translatable("message.some_locators.locator_inspector_empty"), true);
                return InteractionResult.FAIL;
            }

            blockEntity.openScreen(serverPlayer);
            return InteractionResult.SUCCESS;
        } else if (half == LocatorInspectorHalf.BOTTOM) {
            if (stack.isEmpty()) {
                ItemStack newStack = blockEntity.takeItem();

                if (!newStack.isEmpty()) {
                    level.playSound(null, blockEntityPos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level.playSound(null, blockEntityPos, SoundEvents.BUNDLE_INSERT, SoundSource.BLOCKS, 1.0F, 1.0F);
                }

                player.addItem(newStack);
            } else {
                InsertResult insertResult = blockEntity.tryInsertItem(stack.copyWithCount(1));
                switch (insertResult) {
                    case SUCCESS: {
                        level.playSound(null, blockEntityPos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 1.0F, 1.15F);
                        player.getItemInHand(mainHand).consume(1, player);
                        return InteractionResult.CONSUME;
                    }
                    case CONTAINS: {
                        serverPlayer.displayClientMessage(Component.translatable("message.some_locators.locator_inspector_contains"), true);
                        return InteractionResult.FAIL;
                    }
                    default: {
                        return InteractionResult.FAIL;
                    }
                }
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(HALF, FACING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        LocatorInspectorHalf half = state.getValue(HALF);
        return half == LocatorInspectorHalf.BOTTOM ? new LocatorInspectorBlockEntity(pos, state) : null;
    }

    @Override
    protected @NonNull BlockState updateShape(BlockState state, @NonNull LevelReader level,
                                              @NonNull ScheduledTickAccess scheduledTickAccess, @NonNull BlockPos pos,
                                              @NonNull Direction direction, @NonNull BlockPos neighborPos,
                                              @NonNull BlockState neighborState, @NonNull RandomSource random) {
        LocatorInspectorHalf half = state.getValue(HALF);

        Direction directionToOther = half == LocatorInspectorHalf.BOTTOM
                ? Direction.UP
                : Direction.DOWN;

        if (direction == directionToOther) {
            return neighborState.is(this) && neighborState.getValue(HALF) != half
                    ? state
                    : Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, level, scheduledTickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos blockPos = context.getClickedPos();
        Level level = context.getLevel();
        if (blockPos.getY() < level.getMaxY() && level.getBlockState(blockPos.above()).canBeReplaced(context)) {
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection().getOpposite())
                    .setValue(HALF, LocatorInspectorHalf.BOTTOM);
        } else return null;
    }

    @Override
    public void setPlacedBy(@NonNull Level level, @NonNull BlockPos pos, @NonNull BlockState state, @Nullable LivingEntity placer, @NonNull ItemStack stack) {
        level.setBlock(pos.above(), state.setValue(HALF, LocatorInspectorHalf.TOP), LocatorInspectorBlock.UPDATE_CLIENTS);
    }

    @Override
    protected boolean isPathfindable(@NonNull BlockState state, @NonNull PathComputationType pathComputationType) {
        return false;
    }

    public static void setUnused(PacketContext<SetUnusedPayload> context) {
        if (context.message().blockMoved()) return;
        ServerLevel level = context.sender().level();
        BlockEntity block = level.getBlockEntity(context.message().pos());
        if (!(block instanceof LocatorInspectorBlockEntity blockEntity)) {
            Constants.LOG.warn("Couldn't find block entity while trying to set Locator Inspector as unused by player!");
            return;
        }
        blockEntity.unsetCurrentlyUsed();
    }

    public static void writePunchCard(PacketContext<WriteLodestoneComponentPayload> context) {
        ServerLevel level = context.sender().level();
        BlockEntity block = level.getBlockEntity(context.message().locatorInspectorBlockPos());
        if (!(block instanceof LocatorInspectorBlockEntity blockEntity)) {
            Constants.LOG.error("Couldn't replace punch card in Locator Inspector!");
            return;
        }

        blockEntity.setWrittenStack(context.message().writtenStack());
    }
}
