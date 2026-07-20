package me.kveex.somelocators.item;

import me.kveex.somelocators.component.PlayerDistance;
import me.kveex.somelocators.component.PlayerLocatorComponent;
import me.kveex.somelocators.platform.Services;
import me.kveex.somelocators.registry.ModComponents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
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
        PlayerLocatorComponent component = stack.get(ModComponents.PLAYER_TRACKER_COMPONENT.get());
        if (component == null) return;
        if (!component.tracked()) return;

        UUID trackedPlayerUUID = component.gameProfile().id();

        Optional<Player> trackedPlayer = locatePlayer(world.getServer(), trackedPlayerUUID);

        if (trackedPlayer.isEmpty()) {
            setPlayerTracker(stack, new PlayerLocatorComponent(component.gameProfile(), PlayerDistance.NOT_FOUND, true, component.expiryTicks() + 1));
            return;
        }

        boolean isInSameDimension = trackedPlayer.get().level().dimension().equals(world.dimension());

        if (!isInSameDimension) {
            setPlayerTracker(stack, new PlayerLocatorComponent(component.gameProfile(), PlayerDistance.IN_ANOTHER_DIMENSION, true, component.expiryTicks()));
            return;
        }

        double calculatedDistance = entity.position().distanceTo(trackedPlayer.get().position());
        PlayerDistance distanceForComponent = PlayerDistance.fromDistance(calculatedDistance);

        if (world.getGameTime() > component.expiryTicks()) {
            removeEntityTracker(stack);
            return;
        }

        if (distanceForComponent.equals(component.playerDistance())) return;

        setPlayerTracker(stack, new PlayerLocatorComponent(component.gameProfile(), distanceForComponent, true, component.expiryTicks()));
    }

    private Optional<Player> locatePlayer(MinecraftServer server, UUID targetUUID) {
        return Optional.ofNullable(server.getPlayerList().getPlayer(targetUUID));
    }

    @Override
    public @NonNull InteractionResult interactLivingEntity(@NonNull ItemStack stack, @NonNull Player user, @NonNull LivingEntity entity, @NonNull InteractionHand hand) {
        if (!(entity instanceof Player victim)) return InteractionResult.PASS;

        ItemStack itemStack = user.getItemInHand(hand);
        if (!(itemStack.getItem() instanceof PlayerLocatorItem)) return InteractionResult.PASS;

        if (!user.isShiftKeyDown()) {
            user.displayClientMessage(Component.translatable("message.some_locators.sneak_to_set_target"), true);
            return InteractionResult.FAIL;
        }


        PlayerLocatorComponent component = itemStack.get(ModComponents.PLAYER_TRACKER_COMPONENT.get());
        if (component != null && component.tracked()) {
            user.displayClientMessage(Component.translatable("message.some_locators.player_locator_target_set_already", component.gameProfile().name()), true);
            return InteractionResult.FAIL;
        }

        setPlayerTracker(
                itemStack,
                new PlayerLocatorComponent(victim.getGameProfile())
        );

        return InteractionResult.SUCCESS;
    }


    @Override
    public @NonNull InteractionResult use(@NonNull Level world, Player user, @NonNull InteractionHand hand) {
        ItemStack stack = user.getItemInHand(hand);
        PlayerLocatorComponent component = stack.get(ModComponents.PLAYER_TRACKER_COMPONENT.get());
        if (component == null) return InteractionResult.PASS;

        if (component.playerDistance().equals(PlayerDistance.NOT_FOUND)) {
            if (user.isShiftKeyDown()) {
                removeEntityTracker(stack);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        if (component.tracked()) return InteractionResult.PASS;

        long expiryTicks = world.getGameTime() + Services.CONFIG.playerLocatorTrackingTime().get() * 20L;

        PlayerLocatorComponent newComponent = new PlayerLocatorComponent(
                component.gameProfile(),
                component.playerDistance(),
                true,
                expiryTicks
        );

        setPlayerTracker(stack, newComponent);
        return InteractionResult.SUCCESS;
    }

    private static void setPlayerTracker(ItemStack stack, PlayerLocatorComponent playerLocatorComponent) {
        stack.set(
                ModComponents.PLAYER_TRACKER_COMPONENT.get(),
                playerLocatorComponent
        );

        stack.set(
                DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(
                        List.of(),
                        List.of(),
                        List.of(playerLocatorComponent.playerDistance().getSerializedName()),
                        List.of()
                )
        );
    }

    private void removeEntityTracker(ItemStack stack) {
        stack.remove(ModComponents.PLAYER_TRACKER_COMPONENT.get());
        stack.remove(DataComponents.CUSTOM_MODEL_DATA);
    }
}
