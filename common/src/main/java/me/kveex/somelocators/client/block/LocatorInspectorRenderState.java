package me.kveex.somelocators.client.block;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;

public class LocatorInspectorRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState locatorRenderState = new ItemStackRenderState();
    public final ItemStackRenderState punchCardRenderState = new ItemStackRenderState();
    public Direction facing = Direction.NORTH;
}
