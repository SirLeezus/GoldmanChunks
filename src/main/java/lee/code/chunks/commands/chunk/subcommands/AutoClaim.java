package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.lists.Lang;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AutoClaim extends SubCommand {

    @Override
    public String getName() {
        return "autoclaim";
    }

    @Override
    public String getDescription() {
        return "Toggle auto claim to claim chunks as you walk.";
    }

    @Override
    public String getSyntax() {
        return "/chunk autoclaim";
    }

    @Override
    public String getPermission() {
        return "chunk.command.autoclaim";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();

        UUID uuid = player.getUniqueId();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (plugin.getData().isPlayerAutoClaiming(uuid)) {
            plugin.getData().removePlayerAutoClaim(uuid);
            player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_AUTO_CLAIM_DISABLED.getString(null));
        } else {
            plugin.getData().setPlayerAutoClaim(uuid, chunkCord);
            player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_AUTO_CLAIM_ENABLED.getString(null));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
