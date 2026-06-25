package me.kveex.somelocators.network.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jspecify.annotations.NonNull;

import java.util.function.BiConsumer;

public final class Payloads {
    private Payloads() {}

    public static <T extends Payload<T>> void registerType(Class<T> clazz) {
        CustomPacketPayload.Type<@NonNull T> type = Payload.getType(clazz);
        StreamCodec<RegistryFriendlyByteBuf, @NonNull T> codec = Payload.getCodec(clazz);
        PayloadDirection direction = getDirection(clazz);

        switch (direction) {
            case S2C -> PayloadTypeRegistry.playS2C().register(type, codec);
            case C2S -> PayloadTypeRegistry.playC2S().register(type, codec);
        }
    }

    public static <T extends ClientPayload<T>> void registerC2S(
            Class<T> clazz,
            BiConsumer<T, ServerPlayNetworking.Context> handler) {

        CustomPacketPayload.Type<T> type = Payload.getType(clazz);
        PayloadDirection direction = getDirection(clazz);

        if (direction == PayloadDirection.S2C) {
            throw new RuntimeException("Cannot register C2S payload with S2C direction");
        }

        ServerPlayNetworking.registerGlobalReceiver(type, handler::accept);
    }

    public static <T extends ServerPayload<T>> void registerS2C(
            Class<T> clazz,
            BiConsumer<T, ClientPlayNetworking.Context> handler) {
        CustomPacketPayload.Type<T> type = Payload.getType(clazz);
        PayloadDirection direction = getDirection(clazz);

        if (direction == PayloadDirection.C2S) {
            throw new RuntimeException("Cannot register S2C payload with C2S direction");
        }

        ClientPlayNetworking.registerGlobalReceiver(type, handler::accept);
    }

    private static <T extends Payload<T>> PayloadDirection getDirection(Class<T> clazz) {
        if (ClientPayload.class.isAssignableFrom(clazz)) {
            return PayloadDirection.C2S;
        } else if (ServerPayload.class.isAssignableFrom(clazz)) {
            return PayloadDirection.S2C;
        }
        throw new RuntimeException("Unknown payload direction");
    }
}