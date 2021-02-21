package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
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
        return "chunk.command.trustall";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        if (args.length > 1) {

            Player target;

            if (plugin.getPU().getOnlinePlayers().contains(args[1])) {
                target = Bukkit.getPlayer(args[1]);
            } else {
                player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_PLAYER_NOT_ONLINE.getString(new String[]{ args[1] }));
                return;
            }

            if (cache.isTrustedGlobal(player.getUniqueId(), target.getUniqueId())) {
                player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_TRUSTALL_ALREADY_ADDED.getString(new String[] { target.getName() }));
                return;
            }

            cache.addTrustedGlobal(player.getUniqueId(), target.getUniqueId());
            player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_TRUSTALL_ADDED_PLAYER.getString(new String[] { target.getName() }));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
