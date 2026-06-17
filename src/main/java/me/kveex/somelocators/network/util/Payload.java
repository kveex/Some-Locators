package me.kveex.somelocators.network.util;

import me.kveex.somelocators.SomeLocators;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NonNls;
import org.jspecify.annotations.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public interface Payload<T extends Payload<T>> extends CustomPacketPayload {
    Map<Class<?>, CustomPacketPayload.Type<?>> TYPE_CACHE = new ConcurrentHashMap<>();

    // Changes id from class CamelCase name to snake_case name
    static String generateId(Class<?> clazz) {
        return clazz.getSimpleName()
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }

    @SuppressWarnings("unchecked")
    static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> getType(Class<T> clazz) {
        return (CustomPacketPayload.Type<T>) TYPE_CACHE.computeIfAbsent(clazz, c ->
                new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(SomeLocators.MOD_ID, generateId(c)))
        );
    }

    @SuppressWarnings("unchecked")
    static <T> StreamCodec<RegistryFriendlyByteBuf, T> getCodec(Class<T> clazz) {
        try {
            return (StreamCodec<RegistryFriendlyByteBuf, T>) clazz.getField("CODEC").get(null);
        } catch (Exception e) {
            throw new RuntimeException("No public static CODEC field in " + clazz.getName(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    default @NonNls CustomPacketPayload.@NonNull Type<? extends CustomPacketPayload> type() {
        return getType((Class<T>) this.getClass());
    }

    StreamCodec<RegistryFriendlyByteBuf, T> codec();
}