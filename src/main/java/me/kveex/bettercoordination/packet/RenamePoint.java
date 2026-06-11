package me.kveex.bettercoordination.packet;

import me.kveex.bettercoordination.component.PointComponent;

public record RenamePoint(String newName, PointComponent renamedPoint) {
}
