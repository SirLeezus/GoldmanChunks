package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
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
        return "mychunks.command.info";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();

        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunk(chunk);
        String owner = plugin.getSqLite().getChunkOwner(chunkCord);
        SQLite SQL = plugin.getSqLite();

        if (SQL.isAdminChunk(chunkCord)) owner = plugin.getPU().format("&4&lAdmin");

        player.sendMessage(Lang.COMMAND_INFO_HEADER.getString(null));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_INFO_LINE_1.getString(new String[] { owner }));
        player.sendMessage(Lang.COMMAND_INFO_LINE_2.getString(new String[] { chunkCord }));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_INFO_FOOTER.getString(null));

        plugin.getPU().renderChunkBorder(player, chunk, "info");
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
