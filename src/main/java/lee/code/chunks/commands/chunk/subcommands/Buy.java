package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.RenderTypes;
import lee.code.essentials.EssentialsAPI;
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
        EssentialsAPI essentialsAPI = plugin.getEssentialsAPI();
        Cache cache = plugin.getCache();
        PU pu = plugin.getPU();

        UUID uuid = player.getUniqueId();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = pu.formatChunkLocation(chunk);

        if (cache.isChunkClaimed(chunkCord)) {
            UUID ownerUUID = cache.getChunkOwnerUUID(chunkCord);
            String ownerName = Bukkit.getOfflinePlayer(ownerUUID).getName();
            if (cache.isChunkForSale(chunkCord)) {
                if (!uuid.equals(ownerUUID)) {
                    int playerClaimAmount = cache.getClaimedAmount(uuid);
                    int playerMaxClaims = cache.getPlayerMaxClaimAmount(uuid);
                    int ownerClaimAmount = cache.getClaimedAmount(ownerUUID);
                    int ownerMaxClaims = cache.getPlayerMaxClaimAmount(ownerUUID);

                    if (playerClaimAmount < playerMaxClaims) {
                        playerClaimAmount++;
                        ownerClaimAmount = ownerClaimAmount - 1;
                        long balance = essentialsAPI.getBalance(uuid);
                        long price = cache.getChunkPrice(chunkCord);

                        if (balance >= price) {
                            essentialsAPI.withdraw(uuid, price);
                            essentialsAPI.deposit(ownerUUID, price);
                            cache.setChunkOwner(chunkCord, ownerUUID, uuid);
                            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BUY_SUCCESSFUL.getComponent(new String[] { chunkCord, pu.formatAmount(price), ownerName, pu.formatAmount(playerClaimAmount), pu.formatAmount(playerMaxClaims) })));
                            pu.renderChunkBorder(player, chunk, RenderTypes.CLAIM);

                            if (Bukkit.getOfflinePlayer(ownerUUID).isOnline()) {
                                Player owner = Bukkit.getPlayer(ownerUUID);
                                if (owner != null) owner.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BUY_SUCCESSFUL_OWNER.getComponent(new String[] { player.getName(), chunkCord, pu.formatAmount(price), pu.formatAmount(ownerClaimAmount), pu.formatAmount(ownerMaxClaims) })));
                            }
                        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_BUY_BALANCE.getComponent(new String[] { String.valueOf(price), chunkCord, ownerName, String.valueOf(balance) })));
                    } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_CLAIM_MAXED.getComponent(new String[] { pu.formatAmount(playerClaimAmount), pu.formatAmount(playerMaxClaims) })));
                } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_BUY_OWNER.getComponent(null)));
            } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_BUY_NOT_FOR_SALE.getComponent(new String[] { chunkCord, ownerName })));
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_BUY_NOT_CLAIMED.getComponent(new String[] { chunkCord })));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
