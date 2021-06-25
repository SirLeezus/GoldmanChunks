package lee.code.chunks.commands.chunk;

import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.commands.chunk.subcommands.*;
import lee.code.chunks.lists.Lang;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor {
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public CommandManager() {
        subCommands.add(new Claim());
        subCommands.add(new AutoClaim());
        subCommands.add(new UnClaim());
        subCommands.add(new AbandonAllClaims());
        subCommands.add(new MaxClaims());
        subCommands.add(new Trust());
        subCommands.add(new TrustAll());
        subCommands.add(new UnTrust());
        subCommands.add(new UnTrustAll());
        subCommands.add(new Trusted());
        subCommands.add(new Map());
        subCommands.add(new Info());
        subCommands.add(new Manage());
        subCommands.add(new ChunkList());
        subCommands.add(new SetPrice());
        subCommands.add(new Buy());
        subCommands.add(new Teleport());
        subCommands.add(new Admin());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (sender instanceof Player player) {

            if (args.length > 0) {
                for (SubCommand subCommand : subCommands) {
                    if (args[0].equalsIgnoreCase(subCommand.getName())) {
                        if (player.hasPermission(subCommand.getPermission())) subCommand.perform(player, args);
                        else
                            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NO_PERMISSION.getComponent(null)));
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