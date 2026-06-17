package me.kveex.somelocators.network.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public interface ClientPayload<T extends ClientPayload<T>> extends Payload<T> {
    default void send() {
        ClientPlayNetworking.send(this);
    }
}