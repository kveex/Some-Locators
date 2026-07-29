package me.kveex.somelocators.item;

import commonnetwork.api.Dispatcher;
import commonnetwork.networking.data.PacketContext;
import me.kveex.somelocators.CommonConfig;
import me.kveex.somelocators.client.CommonClientClass;
import me.kveex.somelocators.component.LocatorComponent;
import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.network.locator.*;
import me.kveex.somelocators.registry.ModComponents;
import me.kveex.somelocators.registry.ModItems;
import me.kveex.somelocators.registry.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocatorItem extends Item {
    public LocatorItem(Properties settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, @NonNull ServerLevel world, @NonNull Entity entity, @Nullable EquipmentSlot slot) {
        LocatorComponent component = stack.get(ModComponents.LOCATOR_COMPONENT.get());
        if (component == null) return;
        LocatorComponent component2 = component.forWorld(world);
        if (component2 != component || component2.currentPoint().isEmpty()) {
            stack.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.empty(), true));
        } else {
            setTracker(stack, component2.currentPoint().get(), component2.points(), component2.skipPointCheck());
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        LocatorComponent component = stack.get(ModComponents.LOCATOR_COMPONENT.get());
        if (component == null) return false;
        return !component.points().isEmpty() && !CommonConfig.isLocatorGlintDisabled;
    }

    @Override
    public @NonNull InteractionResult useOn(@NonNull UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide()) return InteractionResult.PASS;
        if (!(context.getPlayer() instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;

        ItemStack itemStack = context.getItemInHand();
        if (!(itemStack.getItem() instanceof LocatorItem)) return InteractionResult.PASS;

        BlockPos clickedPos = context.getClickedPos();
        if (!world.getBlockState(clickedPos).is(ModTags.TRACKABLE_BLOCKS)) return InteractionResult.PASS;

        return trySetTracker(world, serverPlayer, itemStack, clickedPos);
    }

    public static ItemStack locatorForTrade(ServerLevel world, Entity entity, RandomSource random) {
        int radiusInChunks = 32;
        Optional<BlockPos> structurePos;
        Level structureWorld = world;
        Component structureName;
        BlockState state;

        MinecraftServer server = world.getServer();
        ItemStack playerLocator = ModItems.PLAYER_LOCATOR_ITEM.get().getDefaultInstance();

        if (world.dimension() == Level.NETHER) {
            ServerLevel netherWorld = server.getLevel(Level.NETHER);
            if (netherWorld == null) return playerLocator;
            structureWorld = netherWorld;

            int i = random.nextIntBetweenInclusive(1, 2);
            if (i == 1) {
                structurePos = locate(world, BuiltinStructures.BASTION_REMNANT, entity.blockPosition(), radiusInChunks);
                state = Blocks.CHISELED_POLISHED_BLACKSTONE.defaultBlockState();
                structureName = Component.translatable("item.some_locators.trade.bastion_remnant");
            } else {
                structurePos = locate(world, BuiltinStructures.FORTRESS, entity.blockPosition(), radiusInChunks);
                state = Blocks.CHISELED_NETHER_BRICKS.defaultBlockState();
                structureName = Component.translatable("item.some_locators.trade.fortress");
            }
        } else {
            structurePos = locate(world, BuiltinStructures.RUINED_PORTAL_STANDARD, entity.blockPosition(), 32);
            structureName = Component.translatable("item.some_locators.trade.ruined_portal");
            state = Blocks.OBSIDIAN.defaultBlockState();
        }

        if (structurePos.isEmpty()) return playerLocator;

        PointComponent currentPoint = new PointComponent(structureName.getString(), state, GlobalPos.of(structureWorld.dimension(), structurePos.get()));
        List<PointComponent> points = List.of(currentPoint);

        ItemStack locator = ModItems.LOCATOR_ITEM.get().getDefaultInstance();

        setTracker(locator, currentPoint, points, true);

        return locator;
    }

    private static Optional<BlockPos> locate(ServerLevel world, ResourceKey<Structure> structureRegistryKey, BlockPos start, int chunkRadius) {
        var registry = world.registryAccess().lookupOrThrow(Registries.STRUCTURE);

        Holder<Structure> entry = registry.get(structureRegistryKey.identifier()).orElse(null);
        if (entry == null) return Optional.empty();

        var found = world.getChunkSource().getGenerator().findNearestMapStructure(
                world,
                HolderSet.direct(entry),
                start,
                chunkRadius,
                false
        );

        return found != null ? Optional.of(found.getFirst()) : Optional.empty();
    }

    private static InteractionResult trySetTracker(Level world, ServerPlayer serverPlayer, ItemStack itemStack, BlockPos blockPos) {
        GlobalPos globalPos = GlobalPos.of(world.dimension(), blockPos);
        Optional<LocatorComponent> optional = getTracker(itemStack);
        if (optional.isEmpty()) return InteractionResult.PASS;
        LocatorComponent component = optional.get();

        Optional<PointComponent> foundTarget = component.getTargetByGlobalPos(globalPos);

        if (foundTarget.isPresent()) {
            setTracker(itemStack, foundTarget.get(), component.points());
            return InteractionResult.PASS;
        } else {
            if (component.points().size() >= CommonConfig.maxLocatorPointsAmount) {
                serverPlayer.displayClientMessage(Component.translatable("message.some_locators.locator_points_limit_hit"), true);
                return InteractionResult.FAIL;
            }

            CreateLocatorPointPayload tracker = new CreateLocatorPointPayload(globalPos, world.getBlockState(blockPos));
            Dispatcher.sendToClient(tracker, serverPlayer);
            return InteractionResult.SUCCESS;
        }
    }

    public static void setTracker(PacketContext<SetLocatorPointPayload> context) {
        ServerPlayer player = context.sender();
        ItemStack itemStack = player.getMainHandItem().isEmpty() ? player.getOffhandItem() : player.getMainHandItem();
        Optional<LocatorComponent> optional = getTracker(itemStack);
        if (optional.isEmpty()) return;
        LocatorComponent locatorComponent = optional.get();

        SetLocatorPointPayload tracker = context.message();
        PointComponent target = tracker.toPointComponent();
        List<PointComponent> targets = new ArrayList<>(locatorComponent.points());
        targets.add(target);

        setTracker(itemStack, target, targets);
    }

    @Override
    public @NonNull InteractionResult use(Level world, @NonNull Player player, @NonNull InteractionHand hand) {
        if (world.isClientSide()) return InteractionResult.PASS;
        return CommonClientClass.openLocatorPointsMenu.consumeClick() ? openLocatorMenu(player, hand) : InteractionResult.PASS;
    }

    public static void changeTargetedPoint(PacketContext<ChangeTargetPointPayload> context) {
        ServerPlayer player = context.sender();
        ItemStack itemStack = player.getMainHandItem().isEmpty() ? player.getOffhandItem() : player.getMainHandItem();
        Optional<LocatorComponent> optional = getTracker(itemStack);
        if (optional.isEmpty()) return;
        LocatorComponent locatorComponent = optional.get();

        setTracker(itemStack, context.message().newTarget(), locatorComponent.points());
    }

    public static void renamePoint(PacketContext<RenamePointPayload> context) {
        ServerPlayer player = context.sender();
        RenamePointPayload point = context.message();
        ItemStack itemStack = player.getMainHandItem().isEmpty() ? player.getOffhandItem() : player.getMainHandItem();
        Optional<LocatorComponent> optional = getTracker(itemStack);
        if (optional.isEmpty()) return;
        LocatorComponent locatorComponent = optional.get();

        var points = new ArrayList<>(locatorComponent.points());
        Optional<PointComponent> optionalCurrentPoint = locatorComponent.currentPoint();

        for (int i = 0; i < points.size(); i++) {
            PointComponent somePoint = points.get(i);
            if (somePoint.target().equals(point.globalPos())) {
                PointComponent replacement = new PointComponent(
                        point.newName(),
                        somePoint.blockState(),
                        somePoint.target(),
                        somePoint.lodestoneTracker()
                );
                points.set(i, replacement);

                PointComponent currentPoint;
                if (optionalCurrentPoint.isPresent() && optionalCurrentPoint.get().target().equals(somePoint.target())) {
                    currentPoint = replacement;
                } else {
                    currentPoint = optionalCurrentPoint.orElse(replacement);
                }

                if (locatorComponent.currentPoint().isPresent()) {
                    if (locatorComponent.currentPoint().get().target().equals(currentPoint.target())) {
                        itemStack.set(
                                DataComponents.ITEM_NAME,
                                Component.translatable("item.some_locators.locator_pointing", currentPoint.name())
                        );
                    }
                }

                setTracker(itemStack, currentPoint, points);

                Optional<LocatorComponent> optional1 = getTracker(itemStack);
                if (optional1.isEmpty()) return;
                LocatorComponent updatedLocatorComponent = optional1.get();

                OpenLocatorScreenPayload openLocatorScreenPayload = new OpenLocatorScreenPayload(updatedLocatorComponent, context.message().currentPage());
                Dispatcher.sendToClient(openLocatorScreenPayload, player);
                return;
            }
        }
    }

    public static void removePoint(PacketContext<RemovePointPayload> context) {
        ServerPlayer player = context.sender();
        RemovePointPayload removePoint = context.message();
        ItemStack itemStack = player.getMainHandItem();
        Optional<LocatorComponent> optional = getTracker(itemStack);
        if (optional.isEmpty()) return;
        LocatorComponent locatorComponent = optional.get();

        var points = new ArrayList<>(locatorComponent.points());
        points.remove(removePoint.removedPoint());
        if (points.isEmpty()) {
            removeTracker(itemStack);
            return;
        }

        Optional<PointComponent> optionalCurrentPoint = locatorComponent.currentPoint();
        PointComponent currentPoint;

        if (optionalCurrentPoint.isPresent()
                && !optionalCurrentPoint.get().target().equals(removePoint.removedPoint().target())) {
            currentPoint = optionalCurrentPoint.get();
        } else {
            currentPoint = points.getFirst();
        }

        setTracker(itemStack, currentPoint, points);
    }

    private static InteractionResult openLocatorMenu(Player player, InteractionHand hand) {
        if (!(player instanceof ServerPlayer serverPlayer)) return InteractionResult.PASS;
        ItemStack itemStack = player.getItemInHand(hand);
        LocatorComponent component = itemStack.get(ModComponents.LOCATOR_COMPONENT.get());

        if (component == null) return InteractionResult.PASS;
        OpenLocatorScreenPayload openLocatorMenu = new OpenLocatorScreenPayload(component);
        Dispatcher.sendToClient(openLocatorMenu, serverPlayer);
        return InteractionResult.SUCCESS;
    }

    private static void setTracker(ItemStack itemStack, PointComponent point, List<PointComponent> points) {
        setTracker(itemStack, point, points, false);
    }

    private static void setTracker(ItemStack itemStack, PointComponent point, List<PointComponent> points, boolean skipPointCheck) {
        itemStack.set(
                ModComponents.LOCATOR_COMPONENT.get(),
                new LocatorComponent(point, points, skipPointCheck)
        );

        itemStack.set(
                DataComponents.LODESTONE_TRACKER,
                point.lodestoneTracker()
        );
    }

    private static void removeTracker(ItemStack itemStack) {
        itemStack.set(ModComponents.LOCATOR_COMPONENT.get(), LocatorComponent.DEFAULT);
        itemStack.remove(DataComponents.LODESTONE_TRACKER);
    }

    private static Optional<LocatorComponent> getTracker(ItemStack itemStack) {
        LocatorComponent locatorComponent = itemStack.get(ModComponents.LOCATOR_COMPONENT.get());
        if (locatorComponent == null) {
            itemStack.set(ModComponents.LOCATOR_COMPONENT.get(), LocatorComponent.DEFAULT);
            LocatorComponent newComponent = itemStack.get(ModComponents.LOCATOR_COMPONENT.get());
            return Optional.ofNullable(newComponent);
        }
        return Optional.of(locatorComponent);
    }

    @Override
    public @NonNull Component getName(ItemStack stack) {
        LocatorComponent component = stack.get(ModComponents.LOCATOR_COMPONENT.get());
        if (component == null || component.currentPoint().isEmpty()) {
            return Component.translatable("item.some_locators.locator");
        }
        return Component.translatable("item.some_locators.locator_pointing", component.currentPoint().get().name());
    }
}
