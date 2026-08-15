package com.lunatech.fastbrewing;

import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BrewingStartEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
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
                // 1. Check real-time active GUI viewers
                boolean allowed = false;
                for (HumanEntity viewer : stand.getInventory().getViewers()) {
                    if (viewer instanceof Player player && player.hasPermission(cfg.permission())) {
                        allowed = true;
                        break;
                    }
                }

                // 2. Fallback to PDC tag set when last interacted
                if (!allowed) {
                    byte tag = stand.getPersistentDataContainer().getOrDefault(plugin.getPermissionKey(), PersistentDataType.BYTE, (byte) 0);
                    if (tag == (byte) 0) {
                        return; // No authorized player viewing or tagged on this stand
                    }
                }
            }
            event.setBrewingTime(cfg.getTargetTicks());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryOpen(InventoryOpenEvent event) {
        updatePermissionTag(event.getInventory(), event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        updatePermissionTag(event.getInventory(), event.getWhoClicked());
    }

    private void updatePermissionTag(Inventory inv, HumanEntity human) {
        FastBrewingConfig cfg = plugin.getBrewingConfig();
        if (!cfg.enabled() || !cfg.permissionRequired()) return;

        if (inv != null && inv.getType() == InventoryType.BREWING && human instanceof Player player) {
            if (inv.getHolder() instanceof BrewingStand stand) {
                byte isAllowed = player.hasPermission(cfg.permission()) ? (byte) 1 : (byte) 0;
                stand.getPersistentDataContainer().set(plugin.getPermissionKey(), PersistentDataType.BYTE, isAllowed);
                stand.update();
            }
        }
    }
}
