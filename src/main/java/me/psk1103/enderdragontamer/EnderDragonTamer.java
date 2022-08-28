package me.psk1103.enderdragontamer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class EnderDragonTamer extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new BlockPlaceListener(this), this);
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
