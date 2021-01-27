package lee.code.mychunks.commands.chunk.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.database.SQLite;
import lee.code.mychunks.files.defaults.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AbandonAllClaims extends SubCommand {

    @Override
    public String getName() {
        return "abandonallclaims";
    }

    @Override
    public String getDescription() {
        return "Unclaim all the chunks you own.";
    }

    @Override
    public String getSyntax() {
        return "/chunk abandonallclaims";
    }

    @Override
    public String getPermission() {
        return "mychunks.command.abandonallclaims";
    }

    @Override
    public void perform(Player player, String[] args) {
        MyChunks plugin = MyChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        UUID uuid = player.getUniqueId();

        if (SQL.hasClaimedChunks(uuid)) {
            SQL.deleteAllClaimedChunks(uuid);
            int maxClaims = SQL.getMaxPlayerClaims(player);
            int totalClaims = SQL.getClaimedAmount(uuid);

            player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.COMMAND_ABANDONALLCLAIMS_SUCCESSFUL.getConfigValue(new String[] { plugin.getUtility().formatAmount(totalClaims), plugin.getUtility().formatAmount(maxClaims) }));
        } else player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_COMMAND_ABANDONALLCLAIMS_NO_CLAIMS.getConfigValue(null));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
