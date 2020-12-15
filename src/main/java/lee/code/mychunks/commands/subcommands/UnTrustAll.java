package lee.code.mychunks.commands.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.database.SQLite;
import lee.code.mychunks.files.defaults.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnTrustAll extends SubCommand {

    @Override
    public String getName() {
        return "untrustall";
    }

    @Override
    public String getDescription() {
        return "Remove a player from your global trust list.";
    }

    @Override
    public String getSyntax() {
        return "/chunk untrustall &f<player>";
    }

    @Override
    public String getPermission() {
        return "mychunks.command.untrustall";
    }

    @Override
    public void perform(Player player, String[] args) {
        MyChunks plugin = MyChunks.getPlugin();
        UUID uuid = player.getUniqueId();
        SQLite SQL = plugin.getSqLite();

        if (args.length > 1) {
            if (SQL.getGlobalTrustedPlayers(uuid).contains(args[1])) {
                SQL.removeGlobalTrustedPlayer(uuid, args[1]);
                player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.COMMAND_UNTRUSTALL_REMOVED_PLAYER.getConfigValue(new String[] { args[1] }));
            } else player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_COMMAND_UNTRUSTALL_PLAYER_NOT_TRUSTED.getConfigValue(new String[] { args[1] }));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
