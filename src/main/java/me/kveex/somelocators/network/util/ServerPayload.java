package me.kveex.somelocators.network.util;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;

public interface ServerPayload<T extends ServerPayload<T>> extends Payload<T> {
    default void send(ServerPlayer player) {
        ServerPlayNetworking.send(player, this);
    }
}