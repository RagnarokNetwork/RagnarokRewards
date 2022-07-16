package net.ragnaroknetwork.rewardsgui.command.commands;

import net.ragnaroknetwork.rewardsgui.RewardsGUI;
import net.ragnaroknetwork.rewardsgui.menu.RewardsMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GUICommand extends Command {
    private final RewardsGUI plugin;

    public GUICommand(RewardsGUI plugin) {
        super("gui");
        setDescription("Opens up the Rewards GUI");
        setUsage("/rewards gui");
        setPermission("rewardsgui.open");

        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        new RewardsMenu(plugin, (Player) sender);
        return true;
    }
}
