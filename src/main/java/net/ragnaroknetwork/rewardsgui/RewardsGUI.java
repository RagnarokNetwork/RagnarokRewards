package net.ragnaroknetwork.rewardsgui;

import net.ragnaroknetwork.rewardsgui.command.RewardsCommand;
import net.ragnaroknetwork.rewardsgui.config.Config;
import net.ragnaroknetwork.rewardsgui.config.ConfigManager;
import net.ragnaroknetwork.rewardsgui.database.Database;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;

public final class RewardsGUI extends JavaPlugin {
    private ConfigManager<Config> configManager;
    private Database database;

    @Override
    public void onEnable() {
        configManager = ConfigManager.create(getLogger(), getDataFolder().toPath(), "config.yml", Config.class);
        configManager.reloadConfig();

        Config configData = configManager.getConfigData();

        database = new Database(configData.database(), getLogger());
        database.loadRewardsDatabase();

        getServer().getPluginManager().registerEvents(new MenuFunctionListener(), this);
        getServer().getPluginCommand("rewards").setExecutor(new RewardsCommand(this));
    }

    @Override
    public void onDisable() {
    }

    public Config getPluginConfig() {
        return configManager.getConfigData();
    }

    public Database getPluginDatabase() {
        return database;
    }
}
