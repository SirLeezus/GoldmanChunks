package lee.code.mychunks.commands.chunk.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.files.defaults.Lang;
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
        return "mychunks.command.unclaim";
    }

    @Override
    public void perform(Player player, String[] args) {
        MyChunks plugin = MyChunks.getPlugin();
        UUID uuid = player.getUniqueId();

        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getUtility().formatChunk(chunk);

        if (plugin.getSqLite().isChunkClaimed(chunkCord)) {
            if (plugin.getSqLite().isChunkOwner(chunkCord, uuid)) {
                plugin.getSqLite().unClaimChunk(chunkCord, uuid);
                player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.COMMAND_UNCLAIM_SUCCESSFUL.getConfigValue(new String[] { chunkCord }));
                plugin.getUtility().renderChunkBorder(player, chunk, "unclaim");

            } else player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_COMMAND_UNCLAIM_OWNER.getConfigValue(new String[] { plugin.getSqLite().getChunkOwner(chunkCord) }));
        } else player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_COMMAND_UNCLAIMED_NOT_CLAIMED.getConfigValue(null));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
