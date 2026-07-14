package me.kveex.somelocators.block.entity;

import com.mojang.authlib.GameProfile;
import me.kveex.somelocators.block.entity.util.ImplementedContainer;
import me.kveex.somelocators.block.entity.util.InsertResult;
import me.kveex.somelocators.component.PlayerTrackerComponent;
import me.kveex.somelocators.network.locatorinspector.OpenCopyScreenPayload;
import me.kveex.somelocators.network.locatorinspector.OpenInspectScreenPayload;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LocatorInspectorBlockEntity extends BlockEntity implements ImplementedContainer {
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

    public InsertResult tryInsertItem(ItemStack stack) {
        if (ACCEPTABLE_LOCATOR.contains(stack.getItem())) {
            if (!this.getLocatorItem().isEmpty()) return InsertResult.CONTAINS;
            this.setItem(LOCATOR, stack);
            setChanged();
            return InsertResult.SUCCESS;
        } else if (stack.is(ModItems.PUNCH_CARD_ITEM)) {
            if (!this.getPunchCard().isEmpty()) return InsertResult.CONTAINS;
            this.setItem(PUNCH_CARD, stack);
            setChanged();
            return InsertResult.SUCCESS;
        } else return InsertResult.FAIL;
    }

    public void setWrittenStack(ItemStack stack) {
        if (!stack.has(ModComponents.LODESTONE_POINT_COMPONENT)) return;

        if (stack.is(ModItems.LOCATOR_ITEM)) {
            this.setItem(LOCATOR, stack);
            setChanged();
        } else if (stack.is(ModItems.PUNCH_CARD_ITEM)) {
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

    public void openScreen(ServerPlayer player) {
        ItemStack locator =  this.getLocatorItem();
        ItemStack punchCard =  this.getPunchCard();

        if (!locator.isEmpty() && !punchCard.isEmpty()) {
            OpenCopyScreenPayload openCopyScreenPayload = new OpenCopyScreenPayload(this.getBlockPos(), locator, punchCard);
            openCopyScreenPayload.send(player);
        } else {
            ItemStack inspectedStack = locator.isEmpty() ? punchCard : locator;
            Optional<GameProfile> optionalGameProfile = Optional.empty();
            PlayerTrackerComponent playerTrackerComponent = inspectedStack.get(ModComponents.PLAYER_TRACKER_COMPONENT);

            if (playerTrackerComponent != null) {
                Player trackedPlayer = player.level().getPlayerInAnyDimension(playerTrackerComponent.trackedPlayerUUID());
                if (trackedPlayer != null) {
                    optionalGameProfile = Optional.of(trackedPlayer.getGameProfile());
                }
            }

            OpenInspectScreenPayload openInspectScreenPayload = new OpenInspectScreenPayload(
                    this.getBlockPos(), inspectedStack,
                    optionalGameProfile
            );
            openInspectScreenPayload.send(player);
        }

        setCurrentlyUsed();
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
        if (this.level instanceof ServerLevel) {
            ClientboundBlockEntityDataPacket packet = this.getUpdatePacket();
            if (packet != null) {
                PlayerLookup.tracking(this).forEach(p -> p.connection.send(packet));
            }
        }
    }
}
