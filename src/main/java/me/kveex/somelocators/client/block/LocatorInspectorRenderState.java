package me.kveex.somelocators.client.block;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.state.ItemClusterRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Display;
import net.minecraft.world.item.ItemStack;

public class LocatorInspectorRenderState extends BlockEntityRenderState {
    public final ItemStackRenderState punchCardRenderState = new ItemStackRenderState();
    public boolean hasPunchCard = false;
    public Direction facing = Direction.NORTH;
}
