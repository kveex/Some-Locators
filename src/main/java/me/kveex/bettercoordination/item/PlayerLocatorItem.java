package me.kveex.bettercoordination.item;

import me.kveex.bettercoordination.BetterCoordination;
import me.kveex.bettercoordination.component.EntityTracker;
import me.kveex.bettercoordination.component.EntityDistance;
import me.kveex.bettercoordination.registry.ModComponents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.CustomModelDataComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
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
        EntityTracker component = stack.get(ModComponents.ENTITY_TRACKER_COMPONENT);
        if (component == null) return;

        Optional<UUID> trackedEntityUUID = component.trackedEntityUUID();
        if (trackedEntityUUID.isEmpty()) return;

        Entity trackedEntity = world.getEntity(trackedEntityUUID.get());
        if (trackedEntity == null) return;
        if (!BetterCoordination.CONFIG.canPlayerLocatorTracksAllEntities() && !(trackedEntity instanceof PlayerEntity)) {
            stack.remove(ModComponents.ENTITY_TRACKER_COMPONENT);
            return;
        }

        double calculatedDistance = entity.getEntityPos().distanceTo(trackedEntity.getEntityPos());
        EntityDistance distanceForComponent = EntityDistance.fromDistance(calculatedDistance);

        if (distanceForComponent.equals(component.entityDistance())) return;

        setEntityTracker(stack, new EntityTracker(trackedEntityUUID.get(), distanceForComponent));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user.getEntityWorld().isClient()) return ActionResult.SUCCESS;
        ItemStack itemStack = user.getStackInHand(hand);
        UUID victimUuid;

        if (!BetterCoordination.CONFIG.canPlayerLocatorTracksAllEntities()) {
            if (!(entity instanceof PlayerEntity victim)) return ActionResult.PASS;
            victimUuid = victim.getUuid();
        } else {
            victimUuid = entity.getUuid();
        }

        Optional<EntityTracker> newPlayerTargetComponent = setEntityTracker(itemStack, new EntityTracker(victimUuid));

        newPlayerTargetComponent.ifPresent(targetComponent -> BetterCoordination.LOGGER.info("Got PlayerTargetComponent {}", newPlayerTargetComponent));

        return ActionResult.SUCCESS;
    }

    private Optional<EntityTracker> setEntityTracker(ItemStack stack, EntityTracker component) {
        EntityTracker entityTracker = stack.set(
                ModComponents.ENTITY_TRACKER_COMPONENT,
                component
        );

        stack.set(
                DataComponentTypes.CUSTOM_MODEL_DATA,
                new CustomModelDataComponent(
                        List.of(),
                        List.of(),
                        List.of(component.entityDistance().asString()),
                        List.of()
                )
        );

        return entityTracker == null ? Optional.empty() : Optional.of(entityTracker);
    }
}
