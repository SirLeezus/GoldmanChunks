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

public class Trust extends SubCommand {

    @Override
    public String getName() {
        return "trust";
    }

    @Override
    public String getDescription() {
        return "Trusts a player to the chunk you're standing on.";
    }

    @Override
    public String getSyntax() {
        return "/chunk trust &f<player>";
    }

    @Override
    public String getPermission() {
        return "chunk.command.trust";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        PU pu = plugin.getPU();

        if (args.length > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[1]);
            if (target != null) {
                Chunk chunk = player.getLocation().getChunk();
                String chunkCord = pu.serializeChunkLocation(chunk);
                UUID targetUUID = target.getUniqueId();
                UUID uuid = player.getUniqueId();

                if (cacheManager.isChunkClaimed(chunkCord)) {
                    if (!cacheManager.isChunkTrusted(chunkCord, targetUUID)) {
                        if (cacheManager.isChunkOwner(chunkCord, uuid)) {
                            if (!targetUUID.equals(uuid)) {
                                cacheManager.addChunkTrusted(chunkCord, target.getUniqueId());
                                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_TRUST_ADDED_PLAYER.getComponent(new String[]{target.getName(), chunkCord})));
                            } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_TRUST_OWNER.getComponent(null)));
                        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_TRUST_NOT_OWNER.getComponent(null)));
                    } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_TRUST_ALREADY_ADDED.getComponent(new String[]{target.getName(), chunkCord})));
                } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_TRUST_NOT_OWNER.getComponent(null)));
            } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[]{args[1]})));
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_TRUST_NO_TARGET_PLAYER.getComponent(null)));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
