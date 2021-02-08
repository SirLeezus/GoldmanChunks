package lee.code.chunks.commands.chunk;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.commands.chunk.subcommands.*;
import lee.code.chunks.lists.Lang;
import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandManager implements CommandExecutor {
    @Getter public final ArrayList<SubCommand> subCommands = new ArrayList<>();

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
        subCommands.add(new Admin());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        GoldmanChunks plugin = GoldmanChunks.getPlugin();

        if (sender instanceof Player) {
            Player p = (Player) sender;

            if (args.length > 0) {
                for (int i = 0; i < getSubCommands().size(); i++) {
                    if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())){
                        //perm check for sub command
                        if (p.hasPermission(getSubCommands().get(i).getPermission())) getSubCommands().get(i).perform(p, args);
                        else p.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NO_PERMISSION.getString(null));
                        return true;
                    }
                }
            }

            //plugin info
            int number = 1;
            List<String> lines = new ArrayList<>();
            lines.add(Lang.MESSAGE_HELP_DIVIDER.getString(null));
            lines.add(Lang.MESSAGE_HELP_TITLE.getString(null));
            lines.add("&r");

            for (int i = 0; i < getSubCommands().size(); i++) {
                //perm check
                if (p.hasPermission(getSubCommands().get(i).getPermission())) {
                    //add command to list
                    lines.add(Lang.MESSAGE_HELP_SUB_COMMAND.getString(new String [] { String.valueOf(number), getSubCommands().get(i).getSyntax(), getSubCommands().get(i).getDescription() }));
                    number++;
                }
            }
            lines.add("&r");
            lines.add(Lang.MESSAGE_HELP_DIVIDER.getString(null));

            for (String line : lines) p.sendMessage(plugin.getpU().format(line));
            return true;

        }
        //console command
        if (args.length > 0) {
            for (int i = 0; i < getSubCommands().size(); i++) {
                if (args[0].equalsIgnoreCase(getSubCommands().get(i).getName())) {
                    getSubCommands().get(i).performConsole(sender, args);
                    return true;
                }
            }
        }
        return true;
    }
}