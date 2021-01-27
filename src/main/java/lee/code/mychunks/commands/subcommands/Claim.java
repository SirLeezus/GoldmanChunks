package lee.code.mychunks.commands.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.database.SQLite;
import lee.code.mychunks.files.defaults.Lang;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Claim extends SubCommand {

    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getDescription() {
        return "Claim the chunk you're standing on.";
    }

    @Override
    public String getSyntax() {
        return "/chunk claim";
    }

    @Override
    public String getPermission() {
        return "mychunks.command.claim";
    }

    @Override
    public void perform(Player player, String[] args) {
        MyChunks plugin = MyChunks.getPlugin();

        if (args.length == 1) {

            UUID uuid = player.getUniqueId();
            Chunk chunk = player.getLocation().getChunk();
            String chunkCord = plugin.getUtility().formatChunk(chunk);
            SQLite SQL = plugin.getSqLite();

            if (!SQL.isChunkClaimed(chunkCord)) {

                int playerClaimAmount = SQL.getClaimedAmount(uuid);
                int playerMaxClaims = SQL.getMaxPlayerClaims(player);

                if (playerClaimAmount < playerMaxClaims) {
                    playerClaimAmount++;
                    SQL.claimChunk(chunkCord, uuid);
                    player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.COMMAND_CLAIM_SUCCESSFUL.getConfigValue(new String[] { chunkCord, plugin.getUtility().formatAmount(playerClaimAmount), plugin.getUtility().formatAmount(playerMaxClaims) }));
                    plugin.getUtility().renderChunkBorder(player, chunk, "claim");
                } else player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_COMMAND_CLAIM_MAXED.getConfigValue(new String[] { plugin.getUtility().formatAmount(playerClaimAmount), plugin.getUtility().formatAmount(playerMaxClaims) }));

            } else player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_CLAIMED.getConfigValue(new String[] { plugin.getSqLite().getChunkOwner(chunkCord) }));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
