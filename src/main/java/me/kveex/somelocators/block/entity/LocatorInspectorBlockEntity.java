package me.kveex.somelocators.block.entity;

import me.kveex.somelocators.block.entity.util.ImplementedContainer;
import me.kveex.somelocators.block.properties.LocatorInspectorMode;
import me.kveex.somelocators.registry.ModBlockEntities;
import me.kveex.somelocators.registry.ModComponents;
import me.kveex.somelocators.registry.ModItems;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;

public class LocatorInspectorBlockEntity extends BlockEntity implements ImplementedContainer {
    //TODO: Переделать блок так, чтобы он принимал один локатор и одну специальную панч карту, в которую записаны
    // точки, копированные с другой карты.
    // Игрок кладёт просто локатор, чтобы посмотреть точки в нём.
    // Игрок кладёт локатор и пустую панч карту, что позволит ему скопировать точки на панч карту
    // Игрок кладёт локатор и заполненную панч карту, что позволит ему перенести точки на локатор
    private static final int LOCATOR = 0;
    private static final int PUNCH_CARD = 1;
    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private boolean currentlyUsed = false;
    private static final List<Item> ACCEPTABLE_LOCATOR = List.of(ModItems.LOCATOR_ITEM, ModItems.PLAYER_LOCATOR_ITEM);

    public LocatorInspectorBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.LOCATOR_INSPECTOR_BLOCK_ENTITY, pos, blockState);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public boolean tryInsertItem(ItemStack stack) {
        if (ACCEPTABLE_LOCATOR.contains(stack.getItem())) {
            this.setItem(LOCATOR, stack);
            setChanged();
            return true;
        } else if (stack.is(ModItems.PUNCH_CARD_ITEM)) {
            this.setItem(PUNCH_CARD, stack);
            setChanged();
            return true;
        } else return false;
    }

    public ItemStack getLocatorItem() {
        return this.getItem(LOCATOR);
    }

    public ItemStack getPunchCard() {
        return this.getItem(PUNCH_CARD);
    }

//    private ItemStack takeLocatorItem() {
//        return this.removeItem(LOCATOR, 1);
//    }
//
//    private ItemStack takePunchCard() {
//        return this.removeItem(PUNCH_CARD, 1);
//    }

    private ItemStack takeLocatorItem() {
        ItemStack item = getLocatorItem();
        this.setItem(LOCATOR, ItemStack.EMPTY);
        setChanged();
        return item;
    }

    private ItemStack takePunchCard() {
        ItemStack item = getPunchCard();
        this.setItem(PUNCH_CARD, ItemStack.EMPTY);
        setChanged();
        return item;
    }

    public ItemStack takeItem() {
        ItemStack takenStack = !this.getPunchCard().isEmpty() ? this.takePunchCard() : this.takeLocatorItem();
        System.out.println("SERVER AFTER REMOVE: " + getPunchCard());
        return takenStack;
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

    public LocatorInspectorMode getMode() {
        ItemStack locator = this.getLocatorItem();
        ItemStack punchCard = this.getPunchCard();

        if (locator.isEmpty() && punchCard.isEmpty()) {
            return LocatorInspectorMode.EMPTY;
        } else if (!locator.isEmpty() && punchCard.isEmpty()) {
            return LocatorInspectorMode.INSPECT_LOCATOR;
        } else if (locator.isEmpty()) {
            return !punchCard.has(ModComponents.LODESTONE_POINT_COMPONENT) ? LocatorInspectorMode.INSPECT_PUNCH_CARD_CLEAN : LocatorInspectorMode.INSPECT_PUNCH_CARD_WRITTEN;
        } else {
            return !punchCard.has(ModComponents.LODESTONE_POINT_COMPONENT) ? LocatorInspectorMode.COPY_ON_CARD : LocatorInspectorMode.COPY_OFF_CARD;
        }
    }

    @Override
    protected void loadAdditional(@NonNull ValueInput input) {
        super.loadAdditional(input);
        Collections.fill(items, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input, items);
    }

    @Override
    protected void saveAdditional(@NonNull ValueOutput output) {
        ContainerHelper.saveAllItems(output, items);
        super.saveAdditional(output);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NonNull CompoundTag getUpdateTag(HolderLookup.@NonNull Provider registries) {
        return this.saveCustomOnly(registries);
    }

    @Override
    public void setChanged() {
        super.setChanged();
//        if (this.level != null && !this.level.isClientSide()) {
//            this.level.sendBlockUpdated(
//                    this.worldPosition,
//                    this.getBlockState(),
//                    this.getBlockState(),
//                    Block.UPDATE_ALL
//            );
//        }
        if (this.level instanceof ServerLevel serverLevel) {
            // Явно отправляем пакет всем игрокам рядом с блоком
            ClientboundBlockEntityDataPacket packet = this.getUpdatePacket();
            if (packet != null) {
                PlayerLookup.tracking(this).forEach(p -> p.connection.send(packet));
            }
        }
    }
}
