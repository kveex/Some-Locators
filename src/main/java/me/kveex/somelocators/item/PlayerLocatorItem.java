package me.kveex.somelocators.item;

import me.kveex.somelocators.component.PlayerTrackerComponent;
import me.kveex.somelocators.component.PlayerDistance;
import me.kveex.somelocators.config.SomeLocatorsConfig;
import me.kveex.somelocators.registry.ModComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PlayerLocatorItem extends Item {
    public PlayerLocatorItem(Properties settings) {
        super(settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, @NonNull ServerLevel world, @NonNull Entity entity, @Nullable EquipmentSlot slot) {
        PlayerTrackerComponent component = stack.get(ModComponents.ENTITY_TRACKER_COMPONENT);
        if (component == null) return;
        if (!component.tracked()) return;

        Optional<UUID> trackedPlayerUUID = component.trackedPlayerUUID();
        if (trackedPlayerUUID.isEmpty()) return;

        Optional<Player> trackedPlayer = locatePlayer(world.getServer(), trackedPlayerUUID.get());

        if (trackedPlayer.isEmpty()) {
            setPlayerTracker(stack, new PlayerTrackerComponent(trackedPlayerUUID.get(), PlayerDistance.NOT_FOUND, true, component.expiryTicks()));
            return;
        }

        boolean isInSameDimension = trackedPlayer.get().level().dimension().equals(world.dimension());

        if (!isInSameDimension) {
            setPlayerTracker(stack, new PlayerTrackerComponent(trackedPlayerUUID.get(), PlayerDistance.IN_ANOTHER_DIMENSION, true, component.expiryTicks()));
            return;
        }

        double calculatedDistance = entity.position().distanceTo(trackedPlayer.get().position());
        PlayerDistance distanceForComponent = PlayerDistance.fromDistance(calculatedDistance);

        if (world.getGameTime() > component.expiryTicks()) {
            removeEntityTracker(stack);
            return;
        }

        if (distanceForComponent.equals(component.playerDistance())) return;

        setPlayerTracker(stack, new PlayerTrackerComponent(trackedPlayerUUID.get(), distanceForComponent, true, component.expiryTicks()));
    }

    private Optional<Player> locatePlayer(MinecraftServer server, UUID targetUUID) {
        return Optional.ofNullable(server.getPlayerList().getPlayer(targetUUID));
    }

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, Player user, @NonNull LivingEntity entity, @NonNull InteractionHand hand) {
        if (user.level().isClientSide()) return InteractionResult.SUCCESS;

        return trySetTrackedPlayer(user, hand, entity);
    }

    private InteractionResult trySetTrackedPlayer(Player user, InteractionHand hand, Entity entity) {
        return trySetTrackedPlayer(user, null, hand, entity, null);
    }

    public static InteractionResult trySetTrackedPlayer(Player user, Level ignoredWorld, InteractionHand hand, Entity entity, EntityHitResult ignoredHitResult) {
        if (!(entity instanceof Player victim)) return InteractionResult.PASS;

        ItemStack itemStack = user.getItemInHand(hand);
        if (!(itemStack.getItem() instanceof PlayerLocatorItem)) return InteractionResult.PASS;

        if (!user.isShiftKeyDown()) {
            user.displayClientMessage(Component.translatable("message.some_locators.sneak_to_set_target"), true);
            return InteractionResult.FAIL;
        }


        PlayerTrackerComponent component = itemStack.get(ModComponents.ENTITY_TRACKER_COMPONENT);
        if (component != null && component.tracked()) {
            user.displayClientMessage(Component.translatable("message.some_locators.player_locator_target_set_already"), true);
            return InteractionResult.FAIL;
        }

        UUID victimUuid = victim.getUUID();

        setPlayerTracker(
                itemStack,
                new PlayerTrackerComponent(victimUuid)
        );

        return InteractionResult.SUCCESS;
    }

    @Override
    public @NonNull InteractionResult use(@NonNull Level world, Player user, @NonNull InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        PlayerTrackerComponent component = stack.get(ModComponents.ENTITY_TRACKER_COMPONENT);
        if (component == null) return InteractionResult.PASS;
        if (component.trackedPlayerUUID().isEmpty()) return InteractionResult.PASS;

        if (component.playerDistance().equals(PlayerDistance.NOT_FOUND)) {
            if (user.isShiftKeyDown()) {
                removeEntityTracker(stack);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        if (component.tracked()) return InteractionResult.PASS;

        long expiryTicks = world.getGameTime() + /*SomeLocators.CONFIG.playerLocatorTrackingTime()*/ SomeLocatorsConfig.playerLocatorTrackingTime * 20L;

        PlayerTrackerComponent newComponent = new PlayerTrackerComponent(
                component.trackedPlayerUUID().get(),
                component.playerDistance(),
                true,
                expiryTicks
        );

        setPlayerTracker(stack, newComponent);
        return InteractionResult.SUCCESS;
    }

    private static void setPlayerTracker(ItemStack stack, PlayerTrackerComponent playerTrackerComponent) {
        stack.set(
                ModComponents.ENTITY_TRACKER_COMPONENT,
                playerTrackerComponent
        );

        stack.set(
                DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(
                        List.of(),
                        List.of(),
                        List.of(playerTrackerComponent.playerDistance().getSerializedName()),
                        List.of()
                )
        );
    }

    private void removeEntityTracker(ItemStack stack) {
        stack.remove(ModComponents.ENTITY_TRACKER_COMPONENT);
        stack.remove(DataComponents.CUSTOM_MODEL_DATA);
    }
}
