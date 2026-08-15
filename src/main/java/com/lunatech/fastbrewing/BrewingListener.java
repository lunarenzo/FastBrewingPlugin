package com.lunatech.fastbrewing;

import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BrewingStartEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public final class BrewingListener implements Listener {

    private final FastBrewingPlugin plugin;
    private boolean paperNativeActive = false;

    public BrewingListener(FastBrewingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBrewingStart(BrewingStartEvent event) {
        FastBrewingConfig cfg = plugin.getBrewingConfig();
        if (!cfg.enabled()) return;
        paperNativeActive = true;

        Block block = event.getBlock();
        if (block != null && cfg.isWorldAllowed(block.getWorld().getName())) {
            event.setBrewingTime(cfg.getTargetTicks());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        FastBrewingConfig cfg = plugin.getBrewingConfig();
        if (!cfg.enabled() || paperNativeActive) return;

        Inventory inv = event.getInventory();
        if (inv.getType() != InventoryType.BREWING) return;

        if (inv.getHolder() instanceof BrewingStand stand) {
            int currentBrewTime = stand.getBrewingTime();
            if (currentBrewTime == 0 || currentBrewTime == 400) {
                if (cfg.isWorldAllowed(stand.getWorld().getName())) {
                    FoliaScheduler.runRegionTask(plugin, stand.getLocation(), () -> {
                        if (stand.getBrewingTime() > cfg.getTargetTicks()) {
                            stand.setBrewingTime(cfg.getTargetTicks());
                            stand.update(true, false);
                        }
                    });
                }
            }
        }
    }
}
