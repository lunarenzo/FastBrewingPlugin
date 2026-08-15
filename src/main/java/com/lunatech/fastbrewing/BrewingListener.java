package com.lunatech.fastbrewing;

import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BrewingStartEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.persistence.PersistentDataType;

public final class BrewingListener implements Listener {

    private final FastBrewingPlugin plugin;

    public BrewingListener(FastBrewingPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBrewingStart(BrewingStartEvent event) {
        FastBrewingConfig cfg = plugin.getBrewingConfig();
        if (!cfg.enabled()) return;

        Block block = event.getBlock();
        if (block != null && cfg.isWorldAllowed(block.getWorld().getName())) {
            if (cfg.permissionRequired() && block.getState() instanceof BrewingStand stand) {
                byte allowed = stand.getPersistentDataContainer().getOrDefault(plugin.getPermissionKey(), PersistentDataType.BYTE, (byte) 1);
                if (allowed == (byte) 0) {
                    return; // Player lacks permission to accelerate brewing
                }
            }
            event.setBrewingTime(cfg.getTargetTicks());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        FastBrewingConfig cfg = plugin.getBrewingConfig();
        if (!cfg.enabled()) return;

        Inventory inv = event.getInventory();
        if (inv.getType() != InventoryType.BREWING) return;

        if (inv.getHolder() instanceof BrewingStand stand) {
            if (cfg.permissionRequired() && event.getWhoClicked() instanceof Player player) {
                byte isAllowed = player.hasPermission(cfg.permission()) ? (byte) 1 : (byte) 0;
                stand.getPersistentDataContainer().set(plugin.getPermissionKey(), PersistentDataType.BYTE, isAllowed);
                stand.update(true, false);
            }
        }
    }
}
