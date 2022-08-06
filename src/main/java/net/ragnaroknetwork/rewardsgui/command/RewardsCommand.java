package net.ragnaroknetwork.rewardsgui.command;

import net.ragnaroknetwork.rewardsgui.RewardsGUI;
import net.ragnaroknetwork.rewardsgui.command.commands.AddCommand;
import net.ragnaroknetwork.rewardsgui.command.commands.GUICommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RewardsCommand implements CommandExecutor {
    private final Map<String, Command> commands = new HashMap<>();

    public RewardsCommand(RewardsGUI plugin) {
        addCommand(new GUICommand(plugin));
        addCommand(new AddCommand(plugin));
    }

    private void addCommand(Command command) {
        if (command.getName() == null)
            throw new IllegalArgumentException("Command name cannot be null : " + command.getClass().getName());
        commands.put(command.getName(), command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        Command subCommand = commands.get(args[0]);

        if (subCommand == null) {
            sender.sendMessage("&cNo command found for " + args[0]);
            sendHelp(sender);
            return true;
        }

        if (subCommand.testPermission(sender))
            subCommand.execute(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    private void sendHelp(CommandSender sender) {
        String commands = "Ragnarok Rewards Commands : \n" +
                this.commands.values().stream()
                        .filter(it -> it.testPermissionSilent(sender))
                        .map(it -> ChatColor.BOLD + it.getName() +
                                ChatColor.RESET + " : " + it.getDescription())
                        .collect(Collectors.joining("\n")) +
                "\n/rewards <command>";
        sender.sendMessage(commands.split("\n"));
    }
}
