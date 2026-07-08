package me.kveex.somelocators.block.entity;

import me.kveex.somelocators.block.entity.util.ImplementedContainer;
import me.kveex.somelocators.block.properties.LocatorInspectorMode;
import me.kveex.somelocators.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

public class LocatorInspectorBlockEntity extends BlockEntity implements ImplementedContainer {
    //TODO: Переделать блок так, чтобы он принимал один локатор и одну специальную панч карту, в которую записаны
    // точки, копированные с другой карты.
    // Игрок кладёт просто локатор, чтобы посмотреть точки в нём.
    // Игрок кладёт локатор и пустую панч карту, что позволит ему скопировать точки на панч карту
    // Игрок кладёт локатор и заполненную панч карту, что позволит ему перенести точки на локатор
    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private boolean currentlyUsed = false;

    public LocatorInspectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.LOCATOR_INSPECTOR_BLOCK_ENTITY, pos, blockState);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public void setItem(LocatorInspectorMode mode, ItemStack stack) {
        int slot = switch (mode) {
            case EMPTY -> 0;
            case INSPECT -> 1;
            default -> -1;
        };

        if (slot < 0) return;

        this.setItem(slot, stack);
    }

    public ItemStack getItem(LocatorInspectorMode mode) {
        int slot = switch (mode) {
            case COPY -> 1;
            case INSPECT -> 0;
            default -> -1;
        };

        if (slot < 0) return ItemStack.EMPTY;

        return this.getItem(slot);
    }

    public ItemStack getFirstItem() {
        return this.getItem(0);
    }

    public ItemStack getSecondItem() {
        return this.getItem(1);
    }

    public boolean isCurrentlyUsed() {
        return this.currentlyUsed;
    }

    public void setCurrentlyUsed() {
        this.currentlyUsed = true;
    }

    public void unsetCurrentlyUsed() {
        this.currentlyUsed = false;
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, items);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        ContainerHelper.saveAllItems(output, items);
        super.saveAdditional(output);
    }
}
