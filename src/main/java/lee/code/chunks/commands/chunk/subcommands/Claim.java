package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
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
        return "chunk.command.claim";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        if (args.length == 1) {
            UUID uuid = player.getUniqueId();
            Chunk chunk = player.getLocation().getChunk();
            String chunkCord = plugin.getPU().formatChunkLocation(chunk);

            if (!cache.isChunkClaimed(chunkCord)) {
                if (!cache.isAdminChunk(chunkCord)) {
                    int playerClaimAmount = cache.getClaimedAmount(uuid);
                    int playerMaxClaims = cache.getPlayerMaxClaimAmount(uuid);

                    if (playerClaimAmount < playerMaxClaims) {
                        playerClaimAmount++;
                        cache.claimChunk(chunkCord, uuid);
                        player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_CLAIM_SUCCESSFUL.getString(new String[] { chunkCord, plugin.getPU().formatAmount(playerClaimAmount), plugin.getPU().formatAmount(playerMaxClaims) }));
                        plugin.getPU().renderChunkBorder(player, chunk, "claim");
                    } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_CLAIM_MAXED.getString(new String[] { plugin.getPU().formatAmount(playerClaimAmount), plugin.getPU().formatAmount(playerMaxClaims) }));
                } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_ADMIN_CLAIMED.getString(null));
            } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_CLAIMED.getString(new String[] { cache.getChunkOwnerName(chunkCord) }));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
