package net.ragnaroknetwork.rewardsgui.command.commands;

import net.ragnaroknetwork.rewardsgui.RewardsGUI;
import net.ragnaroknetwork.rewardsgui.config.Config;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class AddCommand extends Command {
    private final RewardsGUI plugin;

    public AddCommand(RewardsGUI plugin) {
        super("add");
        setDescription("Adds reward to player");
        setPermission("rewardsgui.manage");
        setUsage("/rewards add <reward> [amount] [player]");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        int len = args.length;

        if (len == 0) {
            sender.sendMessage(ChatColor.RED + "Please specify reward id, amount and the Player!");
            return true;
        }

        String rewardId = args[0];
        int amount = len > 1 ? parseInt(args[1]) : 1;
        String target = len > 2 ? args[2] : sender.getName();

        if (amount == -1) {
            sender.sendMessage(ChatColor.RED + "Invalid amount!");
            return true;
        }

        OfflinePlayer player = Arrays.stream(sender.getServer().getOfflinePlayers())
                .filter(it -> it.getName().equals(target))
                .findAny().orElse(null);

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "No player found for " + target);
            return true;
        }

        Config.RewardConfig rewardConfig = plugin.getPluginConfig().rewards().get(rewardId);

        if (rewardConfig == null) {
            sender.sendMessage(ChatColor.RED + "Such reward id is not in the config!");
            return true;
        }

        plugin.getPluginDatabase().getPlayerInventory(player.getUniqueId()).addRewards(rewardId, amount);
        sender.sendMessage(ChatColor.GREEN + "Added " + ChatColor.translateAlternateColorCodes('&', rewardConfig.displayName()) + " Reward to " + target);

        Player onlinePlayer = player.getPlayer();
        if (onlinePlayer != null) {
            onlinePlayer.getOpenInventory().close();
            // new RewardsMenu(plugin, onlinePlayer);
        }
        return true;
    }

    private int parseInt(String num) {
        if (num == null)
            return -1;

        try {
            int d = Integer.parseInt(num);
            return d < 0 ? 1 : d;
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
