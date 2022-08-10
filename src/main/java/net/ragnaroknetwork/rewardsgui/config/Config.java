package net.ragnaroknetwork.rewardsgui.config;

import org.bukkit.Material;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface Config {

    static Map<String, @SubSection RewardConfig> defaultRewards(RewardConfig config) {
        Map<String, RewardConfig> map = new HashMap<>();
        map.put("id", config);
        return map;
    }

    @ConfKey("database")
    @SubSection DatabaseConfig database();

    @ConfKey("gui-rows")
    @ConfComments("Number of rows for the rewards gui. 2 < x <= 6")
    @ConfDefault.DefaultInteger(3)
    int guiRows();

    @ConfKey("rewards")
    @ConfDefault.DefaultObject("defaultRewards")
    Map<String, @SubSection RewardConfig> rewards();

    interface DatabaseConfig {
        @ConfKey("host")
        @ConfDefault.DefaultString("localhost")
        String host();

        @ConfKey("port")
        @ConfDefault.DefaultInteger(3306)
        int port();

        @ConfKey("database")
        @ConfDefault.DefaultString("rewards")
        String database();

        @ConfKey("user")
        @ConfDefault.DefaultString("root")
        String user();

        @ConfKey("password")
        @ConfDefault.DefaultString("1234")
        String password();
    }

    interface RewardConfig {
        @ConfKey("material")
        @ConfDefault.DefaultString("diamond_sword")
        Material material();

        @ConfKey("display-name")
        @ConfDefault.DefaultString("&aSword")
        String displayName();

        @ConfKey("lore")
        @ConfDefault.DefaultStrings("A really op sword")
        List<String> lore();

        @ConfKey("commands")
        @ConfDefault.DefaultStrings("give @p diamond_sword 1")
        List<String> commands();

        @ConfKey("messages")
        @ConfDefault.DefaultString("&cYou just got scammed!")
        List<String> messages();
    }
}
