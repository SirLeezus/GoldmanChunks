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

public class UnTrust extends SubCommand {

    @Override
    public String getName() {
        return "untrust";
    }

    @Override
    public String getDescription() {
        return "Untrust a player from the chunk you're standing on.";
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
        Cache cache = plugin.getCache();

        if (args.length > 1) {
            Chunk chunk = player.getLocation().getChunk();
            String chunkCord = plugin.getPU().formatChunkLocation(chunk);

            UUID target = Bukkit.getPlayerUniqueId(args[1]);

            if (target != null) {
                if (cache.isChunkOwner(chunkCord, uuid)) {
                    if (cache.isChunkTrusted(chunkCord, target)) {
                        cache.removeChunkTrusted(chunkCord, target);
                        player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_UNTRUST_REMOVED_PLAYER.getString(new String[] { args[1], chunkCord }));
                    } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_UNTRUST_PLAYER_NOT_TRUSTED.getString(new String[] { args[1], chunkCord }));
                } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_UNTRUST_NOT_CHUNK_OWNER.getString(null));
            }
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
