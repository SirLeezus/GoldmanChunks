package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.RenderTypes;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnClaim extends SubCommand {

    @Override
    public String getName() {
        return "unclaim";
    }

    @Override
    public String getDescription() {
        return "Unclaims the chunk you're standing on.";
    }

    @Override
    public String getSyntax() {
        return "/chunk unclaim";
    }

    @Override
    public String getPermission() {
        return "chunk.command.unclaim";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        PU pu = plugin.getPU();
        UUID uuid = player.getUniqueId();

        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = pu.serializeChunkLocation(chunk);

        if (cacheManager.isChunkClaimed(chunkCord)) {
            if (cacheManager.isChunkOwner(chunkCord, uuid)) {
                if (!plugin.getData().isPlayerAutoClaiming(uuid)) {
                    cacheManager.unclaimChunk(chunkCord, uuid);
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_UNCLAIM_SUCCESSFUL.getComponent(new String[] { chunkCord })));
                    pu.renderChunkBorder(player, chunk, RenderTypes.UNCLAIM);
                } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_UNCLAIM_WHILE_AUTO_CLAIM.getComponent(null)));
            } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_UNCLAIM_OWNER.getComponent(new String[] { cacheManager.getChunkOwnerName(chunkCord) })));
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_UNCLAIMED_NOT_CLAIMED.getComponent(null)));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
