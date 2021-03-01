package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
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
        return "Trust the player to the chunk you're standing on.";
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
        Cache cache = plugin.getCache();

        if (args.length > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[1]);
            if (target != null) {
                Chunk chunk = player.getLocation().getChunk();
                String chunkCord = plugin.getPU().formatChunkLocation(chunk);
                UUID targetUUID = target.getUniqueId();
                if (!cache.isChunkTrusted(chunkCord, targetUUID)) {
                    if (cache.isChunkClaimed(chunkCord)) {
                        if (cache.isChunkOwner(chunkCord, player.getUniqueId())) {

                            cache.addChunkTrusted(chunkCord, target.getUniqueId());
                            player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_TRUST_ADDED_PLAYER.getString(new String[] { target.getName(), chunkCord }));

                        } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_TRUST_NOT_OWNER.getString(null));
                    } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_TRUST_NOT_OWNER.getString(null));
                } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_TRUST_ALREADY_ADDED.getString(new String[] { target.getName(), chunkCord }));
            } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_PLAYER_NOT_FOUND.getString(new String[]{args[1]}));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
