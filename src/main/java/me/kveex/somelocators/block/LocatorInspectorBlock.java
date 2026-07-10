package me.kveex.somelocators.block;

import com.mojang.serialization.MapCodec;
import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.block.entity.LocatorInspectorBlockEntity;
import me.kveex.somelocators.block.properties.LocatorInspectorHalf;
import me.kveex.somelocators.block.properties.LocatorInspectorMode;
import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.network.OpenLocatorInspectorMenuPayload;
import me.kveex.somelocators.network.SetLocatorInspectorUnused;
import me.kveex.somelocators.registry.ModComponents;
import me.kveex.somelocators.registry.ModItems;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
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
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public class LocatorInspectorBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final EnumProperty<LocatorInspectorHalf> HALF = EnumProperty.create("half", LocatorInspectorHalf.class);
    public static final EnumProperty<LocatorInspectorMode> MODE = EnumProperty.create("mode", LocatorInspectorMode.class);
    private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateHorizontal(Block.boxZ(16.0, 8.0, 16.0));

    public LocatorInspectorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(MODE, LocatorInspectorMode.EMPTY)
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
            SomeLocators.LOGGER.info("Top Half");
            if (stack.is(ModItems.PUNCH_CARD_ITEM)) {
                stack.set(ModComponents.LODESTONE_POINT_COMPONENT, LodestonePointComponent.DEFAULT);
                return InteractionResult.SUCCESS;
            }
            if (blockEntity.getLocatorItem().isEmpty()) {
                player.displayClientMessage(Component.translatable("message.some_locators.locator_inspector_empty"), true);
                return InteractionResult.FAIL;
            }
            OpenLocatorInspectorMenuPayload openLocatorInspectorMenu = new OpenLocatorInspectorMenuPayload(
                    blockEntity.getLocatorItem(),
                    blockEntity.getPunchCard(),
                    blockEntityPos
            );
            openLocatorInspectorMenu.send(serverPlayer);
            blockEntity.setCurrentlyUsed();

        } else if (half == LocatorInspectorHalf.BOTTOM) {
            SomeLocators.LOGGER.info("Bottom Half");
            if (stack.isEmpty()) {
                SomeLocators.LOGGER.info("Empty stack");
                ItemStack newStack = blockEntity.takeItem();
                player.addItem(newStack);

                level.setBlockAndUpdate(blockEntityPos, state.setValue(MODE, blockEntity.getMode()));
                level.playSound(player, blockEntityPos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 1.0F, 0.85F);
                level.playSound(player, blockEntityPos, SoundEvents.BUNDLE_INSERT, SoundSource.BLOCKS, 1.0F, 1.0F);

            } else {
                boolean insertResult = blockEntity.tryInsertItem(stack.copyWithCount(1));
                SomeLocators.LOGGER.info("Insert Result: {}", insertResult);
                SomeLocators.LOGGER.info("Mode serialized name: {}", LocatorInspectorMode.INSPECT_PUNCH_CARD_CLEAN.getSerializedName());
                if (!insertResult) return InteractionResult.FAIL;

                player.getItemInHand(mainHand).consume(1, player);

                LocatorInspectorMode mode = blockEntity.getMode();
                SomeLocators.LOGGER.info("Mode: {}", mode);

                level.setBlockAndUpdate(blockEntityPos, state.setValue(MODE, mode));
                level.playSound(player, blockEntityPos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 1.0F, 1.0F);

            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MODE, HALF, FACING);
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
        level.setBlock(pos.above(), state.setValue(HALF, LocatorInspectorHalf.TOP), 2);
    }

    public static void setUnused(SetLocatorInspectorUnused payload, ServerPlayNetworking.Context access) {
        if (payload.blockMoved()) return;
        ServerLevel level = access.player().level();
        BlockEntity block = level.getBlockEntity(payload.pos());
        if (!(block instanceof LocatorInspectorBlockEntity blockEntity)) {
            SomeLocators.LOGGER.warn("Couldn't find block entity while trying to set Locator Inspector as unused by player!");
            return;
        }
        blockEntity.unsetCurrentlyUsed();
    }
}
