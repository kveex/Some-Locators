package me.kveex.bettercoordination.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.util.math.GlobalPos;

import java.util.Optional;

public record LodestoneTarget(String name, GlobalPos target, LodestoneTrackerComponent tracker) {
    public LodestoneTarget(String name, GlobalPos target) {
        this(name, target, new LodestoneTrackerComponent(Optional.of(target), true));
    }
    public static final Codec<LodestoneTarget> CODEC = RecordCodecBuilder.create(
            builder -> builder.group(
                    Codec.STRING.fieldOf("name").forGetter(LodestoneTarget::name),
                    GlobalPos.CODEC.fieldOf("target").forGetter(LodestoneTarget::target),
                    LodestoneTrackerComponent.CODEC.fieldOf("tracker").forGetter(LodestoneTarget::tracker)
            ).apply(builder, LodestoneTarget::new)
    );
}
