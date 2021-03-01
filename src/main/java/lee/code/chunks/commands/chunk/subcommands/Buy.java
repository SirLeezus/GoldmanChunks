package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Buy extends SubCommand {

    @Override
    public String getName() {
        return "buy";
    }

    @Override
    public String getDescription() {
        return "Buy the chunk you're standing on.";
    }

    @Override
    public String getSyntax() {
        return "/chunk buy";
    }

    @Override
    public String getPermission() {
        return "chunk.command.buy";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        UUID uuid = player.getUniqueId();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (cache.isChunkClaimed(chunkCord)) {
            if (cache.isChunkForSale(chunkCord)) {
                UUID ownerUUID = cache.getChunkOwnerUUID(chunkCord);
                String ownerName = Bukkit.getOfflinePlayer(ownerUUID).getName();
                if (!uuid.equals(ownerUUID)) {
                    int playerClaimAmount = cache.getClaimedAmount(uuid);
                    int playerMaxClaims = cache.getPlayerMaxClaimAmount(uuid);
                    int ownerClaimAmount = cache.getClaimedAmount(ownerUUID);
                    int ownerMaxClaims = cache.getPlayerMaxClaimAmount(ownerUUID);

                    if (playerClaimAmount < playerMaxClaims) {
                        playerClaimAmount++;
                        ownerClaimAmount = ownerClaimAmount - 1;
                        int balance = plugin.getEssentialsAPI().getBalance(uuid);
                        int price = cache.getChunkPrice(chunkCord);

                        if (balance >= price) {
                            plugin.getEssentialsAPI().withdraw(uuid, price);
                            plugin.getEssentialsAPI().deposit(ownerUUID, price);
                            cache.setChunkOwner(chunkCord, ownerUUID, uuid);
                            player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_BUY_SUCCESSFUL.getString(new String[] { chunkCord, plugin.getPU().formatAmount(price), ownerName, plugin.getPU().formatAmount(playerClaimAmount), plugin.getPU().formatAmount(playerMaxClaims) }));
                            plugin.getPU().renderChunkBorder(player, chunk, "claim");

                            if (Bukkit.getOfflinePlayer(ownerUUID).isOnline()) {
                                Player owner = Bukkit.getPlayer(ownerUUID);
                                if (owner != null) owner.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_BUY_SUCCESSFUL_OWNER.getString(new String[] { player.getName(), chunkCord, plugin.getPU().formatAmount(price), plugin.getPU().formatAmount(ownerClaimAmount), plugin.getPU().formatAmount(ownerMaxClaims) }));
                            }
                        }
                    } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_CLAIM_MAXED.getString(new String[] { plugin.getPU().formatAmount(playerClaimAmount), plugin.getPU().formatAmount(playerMaxClaims) }));
                }
            }
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
