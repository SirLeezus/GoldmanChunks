package lee.code.chunks.commands.adminchunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.RenderTypes;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Claim extends SubCommand {

    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getDescription() {
        return "Claims the chunk you're standing on as a admin chunk.";
    }

    @Override
    public String getSyntax() {
        return "/adminchunk claim";
    }

    @Override
    public String getPermission() {
        return "mychunks.admin.claim";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        PU pu = plugin.getPU();

        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = pu.formatChunkLocation(chunk);

        if (!cache.isAdminChunk(chunkCord)) {
            cache.claimAdminChunk(chunkCord);
            pu.renderChunkBorder(player, chunk, RenderTypes.CLAIM);
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_CLAIM_CHUNK.getComponent(new String[] { chunkCord })));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
