package me.kveex.somelocators.item;

import me.kveex.somelocators.SomeLocators;
import me.kveex.somelocators.client.SomeLocatorsClient;
import me.kveex.somelocators.component.LodestonePointComponent;
import me.kveex.somelocators.component.PointComponent;
import me.kveex.somelocators.config.SomeLocatorsConfig;
import me.kveex.somelocators.fun.RandomNullErrorPhrases;
import me.kveex.somelocators.network.*;
import me.kveex.somelocators.registry.ModComponents;
import me.kveex.somelocators.registry.ModItems;
import me.kveex.somelocators.registry.ModTags;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
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
        LodestonePointComponent component = stack.get(ModComponents.LODESTONE_POINT_COMPONENT);
        if (component == null) return;
        LodestonePointComponent component2 = component.forWorld(world);
        if (component2 != component || component2.currentPoint().isEmpty()) {
            stack.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.empty(), true));
        } else {
            setTracker(stack, component2.currentPoint().get(), component2.points(), component2.skipPointCheck());
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        LodestonePointComponent component = stack.get(ModComponents.LODESTONE_POINT_COMPONENT);
        if (component == null) return false;
        return !component.points().isEmpty() && !SomeLocatorsConfig.isLocatorGlintDisabled;
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
        ItemStack playerLocator = ModItems.PLAYER_LOCATOR_ITEM.getDefaultInstance();

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

        ItemStack locator = ModItems.LOCATOR_ITEM.getDefaultInstance();

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
        Optional<LodestonePointComponent> optional = getTracker(itemStack);
        if (optional.isEmpty()) return InteractionResult.PASS;
        LodestonePointComponent component = optional.get();

        Optional<PointComponent> foundTarget = component.getTargetByGlobalPos(globalPos);

        if (foundTarget.isPresent()) {
            setTracker(itemStack, foundTarget.get(), component.points());
            return InteractionResult.PASS;
        } else {
            if (component.points().size() >= SomeLocatorsConfig.maxLocatorPointsAmount) {
                serverPlayer.displayClientMessage(Component.translatable("message.some_locators.locator_points_limit_hit"), true);
                return InteractionResult.FAIL;
            }

            CreateLodestonePoint tracker = new CreateLodestonePoint(globalPos, world.getBlockState(blockPos));
            tracker.send(serverPlayer);
            return InteractionResult.SUCCESS;
        }
    }

    public static void setTracker(SetLodestonePoint tracker, ServerPlayNetworking.Context access) {
        ItemStack itemStack = access.player().getMainHandItem();
        Optional<LodestonePointComponent> optional = getTracker(itemStack);
        if (optional.isEmpty()) return;
        LodestonePointComponent lodestonePointComponent = optional.get();

        PointComponent target = tracker.toPointComponent();
        List<PointComponent> targets = new ArrayList<>(lodestonePointComponent.points());
        targets.add(target);

        setTracker(itemStack, target, targets);
    }

    @Override
    public @NonNull InteractionResult use(Level world, @NonNull Player player, @NonNull InteractionHand hand) {
        if (world.isClientSide()) return InteractionResult.PASS;
        return SomeLocatorsClient.openLocatorPointsMenu.consumeClick() ? openLocatorMenu(player, hand) : InteractionResult.PASS;
    }

    public static void changeTargetedPoint(ChangeTargetPoint changeTargetPoint, ServerPlayNetworking.Context access) {
        ItemStack itemStack = access.player().getMainHandItem();
        Optional<LodestonePointComponent> optional = getTracker(itemStack);
        if (optional.isEmpty()) return;
        LodestonePointComponent lodestonePointComponent = optional.get();

        setTracker(itemStack, changeTargetPoint.newTarget(), lodestonePointComponent.points());
    }

    public static void renamePoint(RenamePoint point, ServerPlayNetworking.Context access) {
        ItemStack itemStack = access.player().getMainHandItem();
        Optional<LodestonePointComponent> optional = getTracker(itemStack);
        if (optional.isEmpty()) return;
        LodestonePointComponent lodestonePointComponent = optional.get();

        var points = new ArrayList<>(lodestonePointComponent.points());
        Optional<PointComponent> optionalCurrentPoint = lodestonePointComponent.currentPoint();

        for (int i = 0; i < points.size(); i++) {
            PointComponent somePoint = points.get(i);
            if (somePoint.target().equals(point.renamedPoint().target())) {
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

                if (lodestonePointComponent.currentPoint().isPresent()) {
                    if (lodestonePointComponent.currentPoint().get().target().equals(currentPoint.target())) {
                        itemStack.set(
                                DataComponents.ITEM_NAME,
                                Component.translatable("item.some_locators.locator_pointing", currentPoint.name())
                        );
                    }
                }

                setTracker(itemStack, currentPoint, points);
                return;
            }
        }
    }

    public static void removePoint(RemovePoint removePoint, ServerPlayNetworking.Context access) {
        ItemStack itemStack = access.player().getMainHandItem();
        Optional<LodestonePointComponent> optional = getTracker(itemStack);
        if (optional.isEmpty()) return;
        LodestonePointComponent lodestonePointComponent = optional.get();

        var points = new ArrayList<>(lodestonePointComponent.points());
        points.remove(removePoint.removedPoint());
        if (points.isEmpty()) {
            removeTracker(itemStack);
            return;
        }

        Optional<PointComponent> optionalCurrentPoint = lodestonePointComponent.currentPoint();
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
        LodestonePointComponent component = itemStack.get(ModComponents.LODESTONE_POINT_COMPONENT);

        if (component == null) return InteractionResult.PASS;
        OpenLocatorMenu openLocatorMenu = new OpenLocatorMenu(component);
        openLocatorMenu.send(serverPlayer);
        return InteractionResult.SUCCESS;
    }

    private static void setTracker(ItemStack itemStack, PointComponent point, List<PointComponent> points) {
        setTracker(itemStack, point, points, false);
    }

    private static void setTracker(ItemStack itemStack, PointComponent point, List<PointComponent> points, boolean skipPointCheck) {
        itemStack.set(
                ModComponents.LODESTONE_POINT_COMPONENT,
                new LodestonePointComponent(point, points, skipPointCheck)
        );

        itemStack.set(
                DataComponents.LODESTONE_TRACKER,
                point.lodestoneTracker()
        );
    }

    private static void removeTracker(ItemStack itemStack) {
        itemStack.set(ModComponents.LODESTONE_POINT_COMPONENT, LodestonePointComponent.DEFAULT);
        itemStack.remove(DataComponents.LODESTONE_TRACKER);
    }

    private static Optional<LodestonePointComponent> getTracker(ItemStack itemStack) {
        LodestonePointComponent lodestonePointComponent = itemStack.get(ModComponents.LODESTONE_POINT_COMPONENT);
        if (lodestonePointComponent == null) {
            itemStack.set(ModComponents.LODESTONE_POINT_COMPONENT, LodestonePointComponent.DEFAULT);
            SomeLocators.LOGGER.warn(RandomNullErrorPhrases.getRandomPhrase("BetterLodestoneTrackerComponent"));
            return Optional.empty();
        }
        return Optional.of(lodestonePointComponent);
    }

    @Override
    public @NonNull Component getName(ItemStack stack) {
        LodestonePointComponent component = stack.get(ModComponents.LODESTONE_POINT_COMPONENT);
        if (component == null || component.currentPoint().isEmpty()) {
            return Component.translatable("item.some_locators.locator");
        }
        return Component.translatable("item.some_locators.locator_pointing", component.currentPoint().get().name());
    }
}
