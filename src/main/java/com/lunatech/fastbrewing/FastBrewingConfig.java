package com.lunatech.fastbrewing;

import java.util.Set;

/**
 * Immutable configuration carrier.
 * Uses primitive specialization and O(1) Set lookups to achieve zero heap allocation spikes.
 */
public record FastBrewingConfig(
    boolean enabled,
    boolean instantBrewing,
    int brewTimeTicks,
    boolean allowAllWorlds,
    Set<String> allowedWorlds
) {
    public int getTargetTicks() {
        return instantBrewing ? 1 : Math.max(1, brewTimeTicks);
    }

    public boolean isWorldAllowed(String worldName) {
        return allowAllWorlds || (allowedWorlds != null && allowedWorlds.contains(worldName));
    }
}
