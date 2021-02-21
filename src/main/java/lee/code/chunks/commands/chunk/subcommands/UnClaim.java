package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.lists.Lang;
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
        return "Unclaim the chunk you're standing on.";
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
        UUID uuid = player.getUniqueId();

        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (plugin.getSqLite().isChunkClaimed(chunkCord)) {
            if (plugin.getSqLite().isChunkOwner(chunkCord, uuid)) {
                plugin.getSqLite().unclaimChunk(chunkCord, uuid, 1);
                player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_UNCLAIM_SUCCESSFUL.getString(new String[] { chunkCord }));
                plugin.getPU().renderChunkBorder(player, chunk, "unclaim");

            } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_UNCLAIM_OWNER.getString(new String[] { plugin.getSqLite().getChunkOwner(chunkCord) }));
        } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_UNCLAIMED_NOT_CLAIMED.getString(null));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
