package me.kveex.somelocators.block;

import com.mojang.serialization.MapCodec;
import me.kveex.somelocators.block.entity.LocatorInspectorBlockEntity;
import me.kveex.somelocators.block.properties.LocatorInspectorMode;
import me.kveex.somelocators.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class LocatorInspectorBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final EnumProperty<LocatorInspectorMode> MODE = EnumProperty.create("mode", LocatorInspectorMode.class);
    public LocatorInspectorBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(MODE, LocatorInspectorMode.EMPTY));
    }

    @Override
    protected @NonNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return simpleCodec(LocatorInspectorBlock::new);
    }

    @Override
    protected @NonNull InteractionResult useWithoutItem(@NonNull BlockState state, Level level, @NonNull BlockPos pos, @NonNull Player player, @NonNull BlockHitResult hitResult) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof LocatorInspectorBlockEntity blockEntity)) return InteractionResult.FAIL;

        InteractionHand mainHand = InteractionHand.MAIN_HAND;
        LocatorInspectorMode mode = state.getValue(MODE);
        ItemStack stack = player.getItemInHand(mainHand);

        if (stack.is(ModItems.LOCATOR_ITEM)) {
            blockEntity.setItem(mode, player.getItemInHand(mainHand).copy());
            player.getItemInHand(mainHand).setCount(0);

            level.setBlockAndUpdate(pos, state.setValue(MODE, mode.getNextMode()));
            level.playSound(player, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 1.0F, 1.0F);

            return InteractionResult.SUCCESS;
        }

        ItemStack newStack = blockEntity.getItem(mode);
        player.addItem(newStack);

        level.setBlockAndUpdate(pos, state.setValue(MODE, mode.getPreviousMode()));
        level.playSound(player, pos, SoundEvents.BUNDLE_INSERT, SoundSource.BLOCKS, 1.0F, 1.0F);

        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MODE);
        builder.add(FACING);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NonNull BlockPos pos, @NonNull BlockState state) {
        return new LocatorInspectorBlockEntity(pos, state);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }
}
