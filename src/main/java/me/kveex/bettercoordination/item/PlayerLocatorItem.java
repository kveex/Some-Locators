package me.kveex.bettercoordination.item;

import me.kveex.bettercoordination.BetterCoordination;
import me.kveex.bettercoordination.component.PlayerTrackerComponent;
import me.kveex.bettercoordination.component.PlayerDistance;
import me.kveex.bettercoordination.registry.ModComponents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerLocatorItem extends Item {
    public PlayerLocatorItem(Settings settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (world.getServer() == null) return;
        PlayerTrackerComponent component = stack.get(ModComponents.ENTITY_TRACKER_COMPONENT);
        if (component == null) return;
        if (!component.tracked()) return;

        Optional<UUID> trackedPlayerUUID = component.trackedPlayerUUID();
        if (trackedPlayerUUID.isEmpty()) return;

        Optional<PlayerEntity> trackedPlayer = locatePlayer(world.getServer(), trackedPlayerUUID.get());

        if (trackedPlayer.isEmpty()) {
            setPlayerTracker(stack, new PlayerTrackerComponent(trackedPlayerUUID.get(), PlayerDistance.NOT_FOUND, true, component.expiryTicks()));
            return;
        }

        boolean isInSameDimension = trackedPlayer.get().getEntityWorld().getRegistryKey().equals(world.getRegistryKey());

        if (!isInSameDimension) {
            setPlayerTracker(stack, new PlayerTrackerComponent(trackedPlayerUUID.get(), PlayerDistance.IN_ANOTHER_DIMENSION, true, component.expiryTicks()));
            return;
        }

        double calculatedDistance = entity.getEntityPos().distanceTo(trackedPlayer.get().getEntityPos());
        PlayerDistance distanceForComponent = PlayerDistance.fromDistance(calculatedDistance);

        if (world.getTime() > component.expiryTicks()) {
            removeEntityTracker(stack);
            return;
        }

        if (distanceForComponent.equals(component.playerDistance())) return;

        setPlayerTracker(stack, new PlayerTrackerComponent(trackedPlayerUUID.get(), distanceForComponent, true, component.expiryTicks()));
    }

    private Optional<PlayerEntity> locatePlayer(MinecraftServer server, UUID targetUUID) {
        return Optional.ofNullable(server.getPlayerManager().getPlayer(targetUUID));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user.getEntityWorld().isClient()) return ActionResult.SUCCESS;

        return trySetTrackedPlayer(user, hand, entity);
    }

    private ActionResult trySetTrackedPlayer(PlayerEntity user, Hand hand, Entity entity) {
        return trySetTrackedPlayer(user, null, hand, entity, null);
    }

    public static ActionResult trySetTrackedPlayer(PlayerEntity user, World ignoredWorld, Hand hand, Entity entity, EntityHitResult ignoredHitResult) {
        if (!(entity instanceof PlayerEntity victim)) return ActionResult.PASS;

        ItemStack itemStack = user.getStackInHand(hand);
        if (!(itemStack.getItem() instanceof PlayerLocatorItem)) return ActionResult.PASS;

        if (!user.isSneaking()) {
            user.sendMessage(Text.translatable("message.better_coordination.sneak_to_set_target"), true);
            return ActionResult.FAIL;
        }


        PlayerTrackerComponent component = itemStack.get(ModComponents.ENTITY_TRACKER_COMPONENT);
        if (component != null && component.tracked()) {
            user.sendMessage(Text.translatable("message.better_coordination.player_locator_target_set_already"), true);
            return ActionResult.FAIL;
        }

        UUID victimUuid = victim.getUuid();

        setPlayerTracker(
                itemStack,
                new PlayerTrackerComponent(victimUuid)
        );

        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        PlayerTrackerComponent component = stack.get(ModComponents.ENTITY_TRACKER_COMPONENT);
        if (component == null) return ActionResult.PASS;
        if (component.trackedPlayerUUID().isEmpty()) return ActionResult.PASS;

        if (component.playerDistance().equals(PlayerDistance.NOT_FOUND)) {
            if (user.isSneaking()) {
                removeEntityTracker(stack);
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }

        if (component.tracked()) return ActionResult.PASS;

        long expiryTicks = world.getTime() + BetterCoordination.CONFIG.playerLocatorTrackingTime() * 20L;

        PlayerTrackerComponent newComponent = new PlayerTrackerComponent(
                component.trackedPlayerUUID().get(),
                component.playerDistance(),
                true,
                expiryTicks
        );

        setPlayerTracker(stack, newComponent);
        return ActionResult.SUCCESS;
    }

    private static void setPlayerTracker(ItemStack stack, PlayerTrackerComponent playerTrackerComponent) {
        stack.set(
                ModComponents.ENTITY_TRACKER_COMPONENT,
                playerTrackerComponent
        );

        stack.set(
                DataComponentTypes.CUSTOM_MODEL_DATA,
                new CustomModelDataComponent(
                        List.of(),
                        List.of(),
                        List.of(playerTrackerComponent.playerDistance().asString()),
                        List.of()
                )
        );
    }

    private void removeEntityTracker(ItemStack stack) {
        stack.remove(ModComponents.ENTITY_TRACKER_COMPONENT);
        stack.remove(DataComponentTypes.CUSTOM_MODEL_DATA);
    }
}
