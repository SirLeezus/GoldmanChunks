package lee.code.chunks.commands.adminchunk;

import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.commands.adminchunk.subcommands.*;
import lee.code.chunks.lists.Lang;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdminCommandManager implements CommandExecutor {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public AdminCommandManager() {
        subCommands.add(new Claim());
        subCommands.add(new UnClaim());
        subCommands.add(new Manage());
        subCommands.add(new Bypass());
        subCommands.add(new Selection());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (sender instanceof Player player) {
            if (args.length > 0) {
                for (SubCommand subCommand : subCommands) {
                    if (args[0].equalsIgnoreCase(subCommand.getName())) {
                        if (player.hasPermission(subCommand.getPermission())) subCommand.perform(player, args);
                        else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NO_PERMISSION.getString(null));
                        return true;
                    }
                }
            }

            int number = 1;
            List<Component> lines = new ArrayList<>();
            lines.add(Lang.MESSAGE_HELP_DIVIDER.getComponent(null));
            lines.add(Lang.MESSAGE_HELP_TITLE.getComponent(null));
            lines.add(Component.text(""));

            for (SubCommand subCommand : subCommands) {
                if (player.hasPermission(subCommand.getPermission())) {
                    lines.add(Lang.MESSAGE_HELP_SUB_COMMAND.getComponent(new String[]{String.valueOf(number), subCommand.getSyntax(), subCommand.getDescription()}));
                    number++;
                }
            }

            lines.add(Component.text(""));
            lines.add(Lang.MESSAGE_HELP_DIVIDER.getComponent(null));

            for (Component line : lines) player.sendMessage(line);
            return true;

        }

        if (args.length > 0) {
            for (SubCommand subCommand : subCommands) {
                if (args[0].equalsIgnoreCase(subCommand.getName())) {
                    subCommand.performConsole(sender, args);
                    return true;
                }
            }
        }
        return true;
    }
}
