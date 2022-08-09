package net.ragnaroknetwork.rewardsgui.menu;

import net.ragnaroknetwork.rewardsgui.RewardsGUI;
import net.ragnaroknetwork.rewardsgui.config.Config;
import net.ragnaroknetwork.rewardsgui.database.PlayerInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.CommandException;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.paginate.PaginatedMenuBuilder;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.SlotSettings;
import org.ipvp.canvas.type.ChestMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RewardsMenu {
    private final static ItemStack nextButton;
    private final static ItemStack nextButtonEmpty;
    private final static ItemStack previousButton;
    private final static ItemStack previousButtonEmpty;

    static {
        nextButton = createButton(Material.MAP, ChatColor.GREEN + "Next");
        nextButtonEmpty = createButton(Material.EMPTY_MAP, ChatColor.GRAY + "Next");
        previousButton = createButton(Material.MAP, ChatColor.GREEN + "Previous");
        previousButtonEmpty = createButton(Material.EMPTY_MAP, ChatColor.GRAY + "Previous");
    }

    private static class RewardItem {
        private final String id;
        private final ItemStack item;

        private RewardItem(String id, ItemStack item) {
            this.id = id;
            this.item = item;
        }
    }

    private static ItemStack createButton(Material material, String name) {
        ItemStack button = new ItemStack(material);
        ItemMeta meta = button.getItemMeta();
        meta.setDisplayName(name);
        button.setItemMeta(meta);
        return button;
    }

    private static RewardItem createReward(String id, Material material, String name, List<String> lore) {
        ItemStack itemStack = new ItemStack(material);
        // RItemStack.of(new ItemStack(material)).asReward(id);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(translate(name));
        meta.setLore(lore.stream().map(RewardsMenu::translate).collect(Collectors.toList()));
        itemStack.setItemMeta(meta);

        return new RewardItem(id, itemStack);
    }

    public static String translate(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    private final RewardsGUI plugin;

    public RewardsMenu(RewardsGUI plugin, Player user) {
        this.plugin = plugin;
        Config config = plugin.getPluginConfig();
        Map<String, Config.RewardConfig> rewardsConfig = config.rewards();

        PlayerInventory inventory = plugin.getPluginDatabase().getPlayerInventory(user.getUniqueId());
        List<SlotSettings> items = new ArrayList<>();

        inventory.getInventoryRewards().stream()
                .map(id -> {
                    Config.RewardConfig rewardConfig = rewardsConfig.get(id);
                    return createReward(id, rewardConfig.material(),
                            rewardConfig.displayName(), rewardConfig.lore());
                }).collect(Collectors.toList())
                .forEach(it -> {
                    int rewards = inventory.getRewards(it.id);
                    for (int i = 0; i < rewards; i++) {
                        items.add(SlotSettings.builder()
                                .item(it.item)
                                .clickOptions(ClickOptions.DENY_ALL)
                                .clickHandler((player, info) -> {
                                    System.out.println(player.getName() + " clicked!");
                                    Config.RewardConfig rewardConfig = config.rewards().get(it.id);
                                    dispatchCommands(rewardConfig.commands(), player, success -> {
                                        if (success) {
                                            inventory.removeReward(it.id);
                                            player.sendMessage(ChatColor.GREEN + "Successfully claimed " + translate(rewardConfig.displayName()) + "!");
                                            info.getClickedMenu().close(player);
                                            new RewardsMenu(plugin, player);
                                        }
                                    });
                                })
                                .build());
                    }
                });

        int guiRows = config.guiRows();
        ChestMenu.Builder menu = ChestMenu.builder(guiRows).title("Rewards").redraw(true);

        BinaryMask.BinaryMaskBuilder slotsBuilder = BinaryMask.builder(menu.getDimensions());
        for (int i = 0; i < guiRows; i++)
            slotsBuilder.pattern("011111110");
        BinaryMask slots = slotsBuilder.build();

        List<Menu> pages = PaginatedMenuBuilder.builder(menu)
                .slots(slots)
                .nextButton(nextButton)
                .nextButtonEmpty(nextButtonEmpty)
                .nextButtonSlot((guiRows - 1) * 9 + 5)
                .previousButton(previousButton)
                .previousButtonEmpty(previousButtonEmpty)
                .previousButtonSlot((guiRows - 1) * 9 + 3)
                .addSlotSettings(items)
                .build();
        pages.get(0).open(user);
    }

    private void dispatchCommands(List<String> commands, Player player, Consumer<Boolean> success) {
        Server server = plugin.getServer();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (String command : commands) {
                        server.dispatchCommand(server.getConsoleSender(),
                                command.replaceAll("@p", player.getName())
                                        .replaceFirst("^/", ""));
                    }

                    success.accept(true);
                } catch (CommandException e) {
                    success.accept(false);
                }
            }
        }.runTask(plugin);
    }
}