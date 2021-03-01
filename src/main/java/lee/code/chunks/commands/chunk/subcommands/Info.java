package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.database.SQLite;
import lee.code.chunks.lists.Lang;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Info extends SubCommand {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Display info about the chunk you're standing on in chat.";
    }

    @Override
    public String getSyntax() {
        return "/chunk info";
    }

    @Override
    public String getPermission() {
        return "chunk.command.info";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);
        String owner = "";

        if (cache.isAdminChunk(chunkCord)) owner = plugin.getPU().format("&4&lAdmin");
        else if (cache.isChunkClaimed(chunkCord)) owner = cache.getChunkOwnerName(chunkCord);

        player.sendMessage(Lang.COMMAND_INFO_HEADER.getString(null));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_INFO_LINE_1.getString(new String[] { owner }));
        player.sendMessage(Lang.COMMAND_INFO_LINE_2.getString(new String[] { chunkCord }));
        if (cache.isChunkForSale(chunkCord)) player.sendMessage(Lang.COMMAND_INFO_LINE_3.getString(new String[] { plugin.getPU().formatAmount(cache.getChunkPrice(chunkCord)) }));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_INFO_FOOTER.getString(null));

        plugin.getPU().renderChunkBorder(player, chunk, "info");
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
