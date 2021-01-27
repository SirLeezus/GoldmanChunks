package lee.code.mychunks.commands.chunk.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.files.defaults.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrustAll extends SubCommand {

    @Override
    public String getName() {
        return "trustall";
    }

    @Override
    public String getDescription() {
        return "Add a player to your global trust list.";
    }

    @Override
    public String getSyntax() {
        return "/chunk trustall &f<player>";
    }

    @Override
    public String getPermission() {
        return "mychunks.command.trustall";
    }

    @Override
    public void perform(Player player, String[] args) {
        MyChunks plugin = MyChunks.getPlugin();

        if (args.length > 1) {

            Player target;

            if (plugin.getUtility().getOnlinePlayers().contains(args[1])) {
                target = Bukkit.getPlayer(args[1]);
            } else {
                player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_PLAYER_NOT_ONLINE.getConfigValue(new String[]{ args[1] }));
                return;
            }

            if (plugin.getSqLite().isGlobalTrusted(player.getUniqueId(), target.getUniqueId())) {
                player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_COMMAND_TRUSTALL_ALREADY_ADDED.getConfigValue(new String[] { target.getName() }));
                return;
            }

            plugin.getSqLite().addGlobalTrustedPlayer(player.getUniqueId(), target.getUniqueId());
            player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.COMMAND_TRUSTALL_ADDED_PLAYER.getConfigValue(new String[] { target.getName() }));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
