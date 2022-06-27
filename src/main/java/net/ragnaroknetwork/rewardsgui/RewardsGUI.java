package net.ragnaroknetwork.rewardsgui;

import net.ragnaroknetwork.rewardsgui.config.Config;
import net.ragnaroknetwork.rewardsgui.config.ConfigManager;
import net.ragnaroknetwork.rewardsgui.database.Database;
import org.bukkit.plugin.java.JavaPlugin;

public final class RewardsGUI extends JavaPlugin {
    private ConfigManager<Config> configManager;
    private Database database;

    @Override
    public void onEnable() {
        configManager = ConfigManager.create(getLogger(), getDataFolder().toPath(), "config.yml", Config.class);
        configManager.reloadConfig();

        Config configData = configManager.getConfigData();

        database = new Database(configData.database(), getLogger());
    }

    @Override
    public void onDisable() {

    }

    public ConfigManager<Config> getPluginConfig() {
        return configManager;
    }

    public Database getPluginDatabase() {
        return database;
    }
}
