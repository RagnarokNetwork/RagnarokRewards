package net.ragnaroknetwork.rewardsgui.config;

import org.bukkit.Material;
import space.arim.dazzleconf.annote.ConfComments;
import space.arim.dazzleconf.annote.ConfDefault;
import space.arim.dazzleconf.annote.ConfKey;
import space.arim.dazzleconf.annote.SubSection;

import java.util.*;

public interface Config {

    static Map<String, @SubSection RewardConfig> defaultRewards(RewardConfig config) {
        Map<String, RewardConfig> map = new HashMap<>();
        map.put("wool", config);
        map.put("sword", new RewardConfig() {
            @Override
            public Material material() {
                return Material.WOOD_SWORD;
            }

            @Override
            public short data() {
                return 30;
            }

            @Override
            public String displayName() {
                return "&cDamaged Sword";
            }

            @Override
            public List<String> lore() {
                return Arrays.asList("Something", "Useless", "POG ?");
            }

            @Override
            public boolean enchanted() {
                return false;
            }

            @Override
            public int amount() {
                return 1;
            }

            @Override
            public List<String> commands() {
                return Collections.singletonList("give @p diamond_sword 1");
            }

            @Override
            public List<String> messages() {
                return Collections.singletonList("&aYou have the brains!");
            }
        });
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
        @ConfDefault.DefaultString("wool")
        Material material();

        @ConfKey("data")
        @ConfDefault.DefaultInteger(9)
        short data();

        @ConfKey("display-name")
        @ConfDefault.DefaultString("&3Something Awesome")
        String displayName();

        @ConfKey("lore")
        @ConfDefault.DefaultStrings("A mystery block")
        List<String> lore();

        @ConfKey("enchanted")
        @ConfDefault.DefaultBoolean(true)
        boolean enchanted();

        @ConfKey("amount")
        @ConfDefault.DefaultInteger(1)
        int amount();

        @ConfKey("commands")
        @ConfDefault.DefaultStrings("give @p dirt 3")
        List<String> commands();

        @ConfKey("messages")
        @ConfDefault.DefaultStrings("&cYou just got scammed! haha bozo O_O")
        List<String> messages();
    }
}
