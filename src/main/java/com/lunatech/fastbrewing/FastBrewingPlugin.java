package com.lunatech.fastbrewing;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

public final class FastBrewingPlugin extends JavaPlugin {

    private volatile FastBrewingConfig config;
    private NamespacedKey permissionKey;

    @Override
    public void onEnable() {
        this.permissionKey = new NamespacedKey(this, "allowed");
        saveDefaultConfig();
        reloadPluginConfig();

        getServer().getPluginManager().registerEvents(new BrewingListener(this), this);
        if (getCommand("fastbrewing") != null) {
            getCommand("fastbrewing").setExecutor(this);
        }
    }

    public void reloadPluginConfig() {
        reloadConfig();
        boolean enabled = getConfig().getBoolean("enabled", true);
        boolean instantBrewing = getConfig().getBoolean("instant-brewing", false);
        int brewTimeTicks = getConfig().getInt("brew-time-ticks", 20);
        boolean permRequired = getConfig().getBoolean("permission-required", false);
        String permNode = getConfig().getString("permission", "fastbrewing.use");

        var worldsList = getConfig().getStringList("allowed-worlds");
        boolean allowAll = worldsList == null || worldsList.contains("*");
        var worldsSet = allowAll ? null : new HashSet<>(worldsList);

        // Atomic lock-free pointer swap
        this.config = new FastBrewingConfig(enabled, instantBrewing, brewTimeTicks, permRequired, permNode, allowAll, worldsSet);
    }

    public FastBrewingConfig getBrewingConfig() {
        return config;
    }

    public NamespacedKey getPermissionKey() {
        return permissionKey;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("fastbrewing.admin")) {
                reloadPluginConfig();
                sender.sendMessage("§6[FastBrewing] §aConfiguration reloaded successfully!");
                return true;
            }
        }
        return false;
    }
}
