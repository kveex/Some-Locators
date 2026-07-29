package me.kveex.somelocators.block.entity;

import commonnetwork.api.Dispatcher;
import me.kveex.somelocators.block.entity.util.ImplementedContainer;
import me.kveex.somelocators.component.LocatorComponent;
import me.kveex.somelocators.component.PlayerLocatorComponent;
import me.kveex.somelocators.network.locatorterminal.OpenCopyScreenPayload;
import me.kveex.somelocators.network.locatorterminal.OpenInspectScreenPayload;
import me.kveex.somelocators.registry.ModBlockEntityTypes;
import me.kveex.somelocators.registry.ModComponents;
import me.kveex.somelocators.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;

public class LocatorTerminalBlockEntity extends BlockEntity implements ImplementedContainer {
    private static final int LOCATOR = 0;
    private static final int PUNCH_CARD = 1;
    private final NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);
    private boolean currentlyUsed = false;
    private static final List<Item> ACCEPTABLE_LOCATOR = List.of(ModItems.LOCATOR_ITEM.get(), ModItems.PLAYER_LOCATOR_ITEM.get(), Items.COMPASS);

    public LocatorTerminalBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntityTypes.LOCATOR_TERMINAL_BLOCK_ENTITY.get(), pos, blockState);
    }

    @Override
    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public InteractionResult tryInsertItem(ItemStack stack) {
        if (ACCEPTABLE_LOCATOR.contains(stack.getItem())) {
            if (!this.getLocatorItem().isEmpty()) return InteractionResult.FAIL;
            this.setItem(LOCATOR, stack);
            setChanged();
            return InteractionResult.CONSUME;
        } else if (stack.is(ModItems.PUNCH_CARD_ITEM.get())) {
            if (!this.getPunchCard().isEmpty()) return InteractionResult.FAIL;
            this.setItem(PUNCH_CARD, stack);
            setChanged();
            return InteractionResult.CONSUME;
        } else return InteractionResult.PASS;
    }

    public void setWrittenStack(ItemStack stack) {
        if (!stack.has(ModComponents.LOCATOR_COMPONENT.get())) return;

        if (stack.is(ModItems.LOCATOR_ITEM.get())) {
            this.setItem(LOCATOR, stack);
            setChanged();
        } else if (stack.is(ModItems.PUNCH_CARD_ITEM.get())) {
            this.setItem(PUNCH_CARD, stack);
            setChanged();
        }
    }

    public ItemStack getLocatorItem() {
        return this.getItem(LOCATOR);
    }

    public ItemStack getPunchCard() {
        return this.getItem(PUNCH_CARD);
    }

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
        return !this.getPunchCard().isEmpty() ? this.takePunchCard() : this.takeLocatorItem();
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

    public InteractionResult openScreen(ServerPlayer player) {
        ItemStack locator = this.getLocatorItem();
        ItemStack punchCard = this.getPunchCard();

        if (!locator.isEmpty() && !punchCard.isEmpty()) {
            if (isDataNotFine(locator)) return InteractionResult.FAIL;
            OpenCopyScreenPayload openCopyScreenPayload = new OpenCopyScreenPayload(
                    this.getBlockPos(), locator, punchCard
            );
            Dispatcher.sendToClient(openCopyScreenPayload, player);
        } else if (locator.isEmpty() && punchCard.isEmpty()) {
          return InteractionResult.PASS;
        } else {
            ItemStack insertedStack = locator.isEmpty() ? punchCard : locator;
            if (isDataNotFine(insertedStack)) return InteractionResult.FAIL;
            OpenInspectScreenPayload openInspectScreenPayload = new OpenInspectScreenPayload(
                    this.getBlockPos(),
                    insertedStack
            );
            Dispatcher.sendToClient(openInspectScreenPayload, player);
        }

        setCurrentlyUsed();
        return InteractionResult.SUCCESS;
    }

    private boolean isDataNotFine(ItemStack stack) {
        LocatorComponent locatorComponent = stack.get(ModComponents.LOCATOR_COMPONENT.get());
        PlayerLocatorComponent playerLocatorComponent = stack.get(ModComponents.PLAYER_LOCATOR_COMPONENT.get());
        if (locatorComponent != null) {
            return locatorComponent.points().isEmpty();
        }

        if (playerLocatorComponent != null) {
            return false;
        }

        return !stack.has(DataComponents.LODESTONE_TRACKER);
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
        if (this.level != null) {
            this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
        }
    }
}
