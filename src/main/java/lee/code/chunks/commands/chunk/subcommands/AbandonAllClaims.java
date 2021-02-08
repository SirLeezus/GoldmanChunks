package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.SQLite;
import lee.code.chunks.lists.Lang;
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
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        UUID uuid = player.getUniqueId();

        if (SQL.hasClaimedChunks(uuid)) {
            SQL.deleteAllClaimedChunks(uuid);
            int maxClaims = SQL.getMaxPlayerClaims(player);
            int totalClaims = SQL.getClaimedAmount(uuid);

            player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_ABANDONALLCLAIMS_SUCCESSFUL.getString(new String[] { plugin.getPU().formatAmount(totalClaims), plugin.getPU().formatAmount(maxClaims) }));
        } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_ABANDONALLCLAIMS_NO_CLAIMS.getString(null));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
