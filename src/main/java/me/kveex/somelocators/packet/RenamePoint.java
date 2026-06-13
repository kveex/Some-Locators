package me.kveex.somelocators.packet;

import me.kveex.somelocators.component.PointComponent;

public record RenamePoint(String newName, PointComponent renamedPoint) {
}
