package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.RenderTypes;
import lee.code.core.util.bukkit.BukkitUtils;
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
        return "Buys the chunk you're standing on.";
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
        CacheManager cacheManager = plugin.getCacheManager();
        PU pu = plugin.getPU();

        UUID uuid = player.getUniqueId();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = pu.serializeChunkLocation(chunk);

        if (cacheManager.isChunkClaimed(chunkCord)) {
            UUID ownerUUID = cacheManager.getChunkOwnerUUID(chunkCord);
            String ownerName = Bukkit.getOfflinePlayer(ownerUUID).getName();
            if (cacheManager.isChunkForSale(chunkCord)) {
                if (!uuid.equals(ownerUUID)) {
                    int playerClaimAmount = cacheManager.getClaimedAmount(uuid);
                    int playerMaxClaims = cacheManager.getPlayerMaxClaimAmount(uuid);
                    int ownerClaimAmount = cacheManager.getClaimedAmount(ownerUUID);
                    int ownerMaxClaims = cacheManager.getPlayerMaxClaimAmount(ownerUUID);

                    if (playerClaimAmount < playerMaxClaims) {
                        playerClaimAmount++;
                        ownerClaimAmount = ownerClaimAmount - 1;
                        long balance = essentialsAPI.getBalance(uuid);
                        long price = cacheManager.getChunkPrice(chunkCord);

                        if (balance >= price) {
                            essentialsAPI.withdraw(uuid, price);
                            essentialsAPI.deposit(ownerUUID, price);
                            cacheManager.setChunkOwner(chunkCord, ownerUUID, uuid);
                            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BUY_SUCCESSFUL.getComponent(new String[] { chunkCord, BukkitUtils.parseValue(price), ownerName, BukkitUtils.parseValue(playerClaimAmount), BukkitUtils.parseValue(playerMaxClaims) })));
                            pu.renderChunkBorder(player, chunk, RenderTypes.CLAIM);

                            if (Bukkit.getOfflinePlayer(ownerUUID).isOnline()) {
                                Player owner = Bukkit.getPlayer(ownerUUID);
                                if (owner != null) owner.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BUY_SUCCESSFUL_OWNER.getComponent(new String[] { player.getName(), chunkCord, BukkitUtils.parseValue(price), BukkitUtils.parseValue(ownerClaimAmount), BukkitUtils.parseValue(ownerMaxClaims) })));
                            }
                        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_BUY_BALANCE.getComponent(new String[] { String.valueOf(price), chunkCord, ownerName, String.valueOf(balance) })));
                    } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_CLAIM_MAXED.getComponent(new String[] { BukkitUtils.parseValue(playerClaimAmount), BukkitUtils.parseValue(playerMaxClaims) })));
                } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_BUY_OWNER.getComponent(null)));
            } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_BUY_NOT_FOR_SALE.getComponent(new String[] { chunkCord, ownerName })));
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_BUY_NOT_CLAIMED.getComponent(new String[] { chunkCord })));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
