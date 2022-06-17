package lee.code.chunks.commands.adminchunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.RenderTypes;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnClaim extends SubCommand {

    @Override
    public String getName() {
        return "unclaim";
    }

    @Override
    public String getDescription() {
        return "Unclaims the chunk you're standing on as a admin chunk.";
    }

    @Override
    public String getSyntax() {
        return "/adminchunk unclaim";
    }

    @Override
    public String getPermission() {
        return "mychunks.admin.unclaim";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        PU pu = plugin.getPU();

        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);

        if (cacheManager.isAdminChunk(chunkCord)) {
            cacheManager.unclaimAdminChunk(chunkCord);
            pu.renderChunkBorder(player, chunk, RenderTypes.UNCLAIM);
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_UNCLAIM_CHUNK.getComponent(new String[] { chunkCord })));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
