package me.kveex.bettercoordination.item;

import io.wispforest.owo.network.ServerAccess;
import me.kveex.bettercoordination.BetterCoordination;
import me.kveex.bettercoordination.component.LodestonePointComponent;
import me.kveex.bettercoordination.component.PointComponent;
import me.kveex.bettercoordination.fun.RandomNullErrorPhrases;
import me.kveex.bettercoordination.packet.*;
import me.kveex.bettercoordination.registry.ModComponents;
import me.kveex.bettercoordination.registry.ModItems;
import me.kveex.bettercoordination.registry.ModNetworking;
import me.kveex.bettercoordination.registry.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureKeys;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocatorItem extends Item {
    public LocatorItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        LodestonePointComponent component = stack.get(ModComponents.LODESTONE_POINT_COMPONENT);
        if (component == null) return;
        LodestonePointComponent component2 = component.forWorld(world);
        if (component2 != component || component2.currentPoint().isEmpty()) {
            stack.set(DataComponentTypes.LODESTONE_TRACKER, new LodestoneTrackerComponent(Optional.empty(), true));
        } else {
            setTracker(stack, component2.currentPoint().get(), component2.points());
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        LodestonePointComponent component = stack.get(ModComponents.LODESTONE_POINT_COMPONENT);
        if (component == null) return false;
        return !component.points().isEmpty() && !BetterCoordination.CONFIG.isLocatorGlintDisabled();
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return useOnBlock(context.getPlayer(), context.getWorld(), context.getHand(), context.getBlockPos());
    }

    public static ItemStack locatorForTrade(ServerWorld world, Entity entity, Random random) {
        int radiusInChunks = 32;
        Optional<BlockPos> structurePos;
        World structureWorld = world;
        Text structureName;
        BlockState state;

        MinecraftServer server = world.getServer();
        ItemStack playerLocator = ModItems.PLAYER_LOCATOR_ITEM.getDefaultStack();
        if (server == null) return playerLocator;

        if (world.getRegistryKey() == World.NETHER) {
            ServerWorld netherWorld = server.getWorld(World.NETHER);
            if (netherWorld == null) return playerLocator;
            structureWorld = netherWorld;

            int i = random.nextBetween(1, 2);
            if (i == 1) {
                structurePos = locate(world, StructureKeys.BASTION_REMNANT, entity.getBlockPos(), radiusInChunks);
                state = Blocks.CHISELED_POLISHED_BLACKSTONE.getDefaultState();
                structureName = Text.translatable("item.better_coordination.trade.bastion_remnant");
            } else {
                structurePos = locate(world, StructureKeys.FORTRESS, entity.getBlockPos(), radiusInChunks);
                state = Blocks.CHISELED_NETHER_BRICKS.getDefaultState();
                structureName = Text.translatable("item.better_coordination.trade.fortress");
            }
        } else {
            structurePos = locate(world, StructureKeys.RUINED_PORTAL, entity.getBlockPos(), 32);
            structureName = Text.translatable("item.better_coordination.trade.ruined_portal");
            state = Blocks.OBSIDIAN.getDefaultState();
        }

        if (structurePos.isEmpty()) return playerLocator;

        PointComponent currentPoint = new PointComponent(structureName.getString(), state, GlobalPos.create(structureWorld.getRegistryKey(), structurePos.get()));
        List<PointComponent> points = List.of(currentPoint);

        ItemStack locator = ModItems.LOCATOR_ITEM.getDefaultStack();

        setTracker(locator, currentPoint, points, true);

        return locator;
    }

    private static Optional<BlockPos> locate(ServerWorld world, RegistryKey<Structure> structureRegistryKey, BlockPos start, int chunkRadius) {
        var registry = world.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);

        RegistryEntry<Structure> entry = registry.getEntry(structureRegistryKey.getValue()).orElse(null);
        if (entry == null) return Optional.empty();

        var found = world.getChunkManager().getChunkGenerator().locateStructure(
                world,
                RegistryEntryList.of(entry),
                start,
                chunkRadius,
                false
        );

        return found != null ? Optional.of(found.getFirst()) : Optional.empty();
    }

    public static ActionResult useOnBlock(PlayerEntity player, World world, Hand hand, HitResult hitResult) {
        return useOnBlock(player, world, hand, BlockPos.ofFloored(hitResult.getPos()));
    }

    private static ActionResult useOnBlock(PlayerEntity player, World world, Hand hand, BlockPos blockPos) {
        if (world.isClient()) return ActionResult.PASS;

        BlockState blockState = world.getBlockState(blockPos);
        if (!blockState.isIn(ModTags.TRACKABLE_BLOCKS)) return ActionResult.PASS;

        GlobalPos globalPos = GlobalPos.create(world.getRegistryKey(), blockPos);
        ItemStack itemStack = player.getStackInHand(hand);

        if (!(itemStack.getItem() instanceof LocatorItem)) return ActionResult.PASS;

        Optional<LodestonePointComponent> optional = getTracker(itemStack);
        if (optional.isEmpty()) return ActionResult.PASS;
        LodestonePointComponent component = optional.get();

        Optional<PointComponent> foundTarget = component.getTargetByGlobalPos(globalPos);

        if (foundTarget.isPresent()) {
            setTracker(itemStack, foundTarget.get(), component.points());
            return ActionResult.PASS;
        } else {
            if (component.points().size() >= BetterCoordination.CONFIG.maxLocatorPointsAmount()) {
                player.sendMessage(Text.translatable("message.better_coordination.locator_points_limit_hit"), true);
                return ActionResult.FAIL;
            }

            CreateLodestonePoint tracker = new CreateLodestonePoint(globalPos, world.getBlockState(blockPos));
            ModNetworking.MOD_CHANNEL.serverHandle(player).send(tracker);

            return ActionResult.SUCCESS;
        }
    }

    public static void setTracker(SetLodestonePoint tracker, ServerAccess access) {
        ItemStack itemStack = access.player().getMainHandStack();
        Optional<LodestonePointComponent> optional = getTracker(itemStack);
        if (optional.isEmpty()) return;
        LodestonePointComponent lodestonePointComponent = optional.get();

        PointComponent target = tracker.toPointComponent();
        List<PointComponent> targets = new ArrayList<>(lodestonePointComponent.points());
        targets.add(target);

        setTracker(itemStack, target, targets);
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (world.isClient()) return ActionResult.PASS;
        return player.isSneaking() ? openLocatorMenu(player, hand) : ActionResult.PASS;
    }

    public static void changeTargetedPoint(ChangeTargetPoint changeTargetPoint, ServerAccess access) {
        ItemStack itemStack = access.player().getMainHandStack();
        Optional<LodestonePointComponent> optional = getTracker(itemStack);
        if (optional.isEmpty()) return;
        LodestonePointComponent lodestonePointComponent = optional.get();

        setTracker(itemStack, changeTargetPoint.newTarget(), lodestonePointComponent.points());
    }

    public static void renamePoint(RenamePoint point, ServerAccess access) {
        ItemStack itemStack = access.player().getMainHandStack();
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

                setTracker(itemStack, currentPoint, points);
                return;
            }
        }
    }

    public static void removePoint(RemovePoint removePoint, ServerAccess access) {
        ItemStack itemStack = access.player().getMainHandStack();
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

    private static ActionResult openLocatorMenu(PlayerEntity player, Hand hand) {
        ItemStack itemStack = player.getStackInHand(hand);
        LodestonePointComponent component = itemStack.get(ModComponents.LODESTONE_POINT_COMPONENT);

        if (component == null) return ActionResult.PASS;
        ModNetworking.MOD_CHANNEL.serverHandle(player).send(new OpenLocatorMenu(component));
        return ActionResult.SUCCESS;
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
                DataComponentTypes.LODESTONE_TRACKER,
                point.lodestoneTracker()
        );
    }

    private static void removeTracker(ItemStack itemStack) {
        itemStack.set(ModComponents.LODESTONE_POINT_COMPONENT, LodestonePointComponent.DEFAULT);
        itemStack.remove(DataComponentTypes.LODESTONE_TRACKER);
    }

    private static Optional<LodestonePointComponent> getTracker(ItemStack itemStack) {
        LodestonePointComponent lodestonePointComponent = itemStack.get(ModComponents.LODESTONE_POINT_COMPONENT);
        if (lodestonePointComponent == null) {
            itemStack.set(ModComponents.LODESTONE_POINT_COMPONENT, LodestonePointComponent.DEFAULT);
            BetterCoordination.LOGGER.warn(RandomNullErrorPhrases.getRandomPhrase("BetterLodestoneTrackerComponent"));
            return Optional.empty();
        }
        return Optional.of(lodestonePointComponent);
    }

    @Override
    public Text getName(ItemStack stack) {
        LodestonePointComponent component = stack.get(ModComponents.LODESTONE_POINT_COMPONENT);
        if (component == null || component.currentPoint().isEmpty()) {
            return Text.translatable("item.better_coordination.locator");
        }
        return Text.translatable("item.better_coordination.locator_pointing", component.currentPoint().get().name());
    }
}
