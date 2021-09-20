package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.Data;
import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
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
        PU pu = plugin.getPU();
        Data data = plugin.getData();

        UUID uuid = player.getUniqueId();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = pu.formatChunkLocation(chunk);
        String worldName = player.getWorld().getName();

        if (data.getWhitelistedWorlds().contains(worldName)) {
            if (data.isPlayerAutoClaiming(uuid)) {
                data.removePlayerAutoClaim(uuid);
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_AUTO_CLAIM_DISABLED.getComponent(null)));
            } else {
                data.setPlayerAutoClaim(uuid, chunkCord);
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_AUTO_CLAIM_ENABLED.getComponent(null)));
            }
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_WORLD_SUPPORT.getComponent(null)));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
