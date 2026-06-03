package me.kveex.bettercoordination.item;

import me.kveex.bettercoordination.BetterCoordination;
import me.kveex.bettercoordination.component.BetterLodestoneTrackerComponent;
import me.kveex.bettercoordination.component.LodestoneTarget;
import me.kveex.bettercoordination.fun.RandomNullErrorPhrases;
import me.kveex.bettercoordination.registry.ModComponents;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LocatorItem extends CompassItem {
    public LocatorItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return !BetterCoordination.CONFIG.isLocatorGlintDisabled();
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResult.PASS;

        World world = context.getWorld();
        if (world.isClient()) return ActionResult.SUCCESS;

        BlockPos blockPos = context.getBlockPos();
        //TODO: Сделать тег с блоками, которые можно выслеживать
        if (!world.getBlockState(blockPos).isOf(Blocks.LODESTONE)) return ActionResult.PASS;

        GlobalPos globalPos = GlobalPos.create(world.getRegistryKey(), blockPos);
        ItemStack itemStack = context.getStack();

        BetterLodestoneTrackerComponent component = itemStack.get(ModComponents.BETTER_TRACKER_COMPONENT);
        if (component == null) {
            itemStack.set(ModComponents.BETTER_TRACKER_COMPONENT, new BetterLodestoneTrackerComponent(Optional.empty(), Collections.emptyList()));
            BetterCoordination.LOGGER.warn(RandomNullErrorPhrases.getRandomPhrase("BetterLodestoneTrackerComponent"));
            return ActionResult.PASS;
        }

        Optional<LodestoneTarget> foundTarget = component.getTargetByGlobalPos(globalPos);

        if (foundTarget.isPresent()) {
            player.sendMessage(Text.translatable("message.better_coordination.known_place", foundTarget.get().name()), true);
            setTracker(itemStack, foundTarget.get(), component.targets());
            return ActionResult.PASS;
        } else {
            if (component.targets().size() >= BetterCoordination.CONFIG.maxLocatorTargetsAmount()) {
                player.sendMessage(Text.translatable("message.better_coordination.locator_targets_limit_hit"), true);
                return ActionResult.FAIL;
            }
            //TODO: Сделать так, чтобы открывался интерфейс и можно было задать имя
            Integer devName = component.targets().size() + 1;
            LodestoneTarget target = new LodestoneTarget(String.valueOf(devName), globalPos);
            List<LodestoneTarget> targets = new ArrayList<>(component.targets());
            targets.add(target);

            setTracker(itemStack, target, targets);

            return ActionResult.SUCCESS;
        }
    }

    @Override
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!player.isSneaking()) return ActionResult.PASS;

        ItemStack itemStack = player.getStackInHand(hand);
        BetterLodestoneTrackerComponent component = itemStack.get(ModComponents.BETTER_TRACKER_COMPONENT);

        if (component == null) return ActionResult.PASS;

        Optional<LodestoneTarget> trackerTarget = component.currentTarget();

        if (component.targets().size() <= 1 || trackerTarget.isEmpty()) return ActionResult.PASS;

        int nextTargetIndex = component.targets().indexOf(trackerTarget.get()) + 1;
        if (nextTargetIndex >= component.targets().size()) nextTargetIndex = 0;

        LodestoneTarget target = component.targets().get(nextTargetIndex);

        setTracker(itemStack, target, component.targets());
        player.sendMessage(Text.translatable("item.better_coordination.targeting_to", target.name()), true);

        return ActionResult.SUCCESS;
    }

    private void setTracker(ItemStack itemStack, LodestoneTarget target, List<LodestoneTarget> targets) {
        itemStack.set(
                ModComponents.BETTER_TRACKER_COMPONENT,
                new BetterLodestoneTrackerComponent(Optional.of(target), targets)
        );

        itemStack.set(
                DataComponentTypes.LODESTONE_TRACKER,
                target.tracker()
        );
    }
}
