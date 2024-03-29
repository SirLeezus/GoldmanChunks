package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnTrust extends SubCommand {

    @Override
    public String getName() {
        return "untrust";
    }

    @Override
    public String getDescription() {
        return "Untrusts a player from the chunk you're standing on.";
    }

    @Override
    public String getSyntax() {
        return "/chunk untrust &f<player>";
    }

    @Override
    public String getPermission() {
        return "chunk.command.untrust";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        UUID uuid = player.getUniqueId();
        CacheManager cacheManager = plugin.getCacheManager();
        PU pu = plugin.getPU();

        if (args.length > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[1]);
            if (target != null) {
                Chunk chunk = player.getLocation().getChunk();
                String chunkCord = pu.serializeChunkLocation(chunk);
                UUID targetUUID = target.getUniqueId();
                if (cacheManager.isChunkOwner(chunkCord, uuid)) {
                    if (cacheManager.isChunkTrusted(chunkCord, targetUUID)) {
                        cacheManager.removeChunkTrusted(chunkCord, targetUUID);
                        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_UNTRUST_REMOVED_PLAYER.getComponent(new String[] { target.getName(), chunkCord })));
                    } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_UNTRUST_PLAYER_NOT_TRUSTED.getComponent(new String[] { target.getName(), chunkCord })));
                } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_UNTRUST_NOT_CHUNK_OWNER.getComponent(null)));
            } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[]{args[1]})));
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_UNTRUST_NO_TARGET_PLAYER.getComponent(null)));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
