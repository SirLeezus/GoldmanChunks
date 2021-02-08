package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.SQLite;
import lee.code.chunks.lists.Lang;
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
        return "mychunks.command.untrust";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        UUID uuid = player.getUniqueId();
        SQLite SQL = plugin.getSqLite();

        if (args.length > 1) {
            Chunk chunk = player.getLocation().getChunk();
            String chunkCord = plugin.getpU().formatChunk(chunk);

            if (SQL.isChunkOwner(chunkCord, uuid)) {
                if (SQL.getTrustedToChunk(chunkCord).contains(args[1])) {
                    SQL.removeChunkTrusted(chunkCord, args[1]);
                    player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_UNTRUST_REMOVED_PLAYER.getString(new String[] { args[1], chunkCord }));
                } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_UNTRUST_PLAYER_NOT_TRUSTED.getString(new String[] { args[1], chunkCord }));
            } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_UNTRUST_NOT_CHUNK_OWNER.getString(null));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
