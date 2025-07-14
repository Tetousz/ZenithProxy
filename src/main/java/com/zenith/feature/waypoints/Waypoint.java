package com.zenith.feature.waypoints;

import com.zenith.mc.dimension.DimensionData;
import com.zenith.mc.dimension.DimensionRegistry;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public record Waypoint(
    String id,
    String dimension,
    int x,
    int y,
    int z
) implements Comparable<Waypoint> {

    public @Nullable DimensionData dimensionData() {
        return DimensionRegistry.REGISTRY.get(dimension);
    }

    @Override
    public int compareTo(@NotNull final Waypoint o) {
        int dim = dimension.compareTo(o.dimension);
        if (dim != 0) return dim;
        return id.compareTo(o.id);
    }
}
