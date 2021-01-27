package lee.code.mychunks.commands.chunk.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.database.SQLite;
import lee.code.mychunks.files.defaults.Lang;
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
        MyChunks plugin = MyChunks.getPlugin();

        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getUtility().formatChunk(chunk);
        String owner = plugin.getSqLite().getChunkOwner(chunkCord);
        SQLite SQL = plugin.getSqLite();

        if (SQL.isAdminChunk(chunkCord)) owner = plugin.getUtility().format("&4&lAdmin");

        player.sendMessage(Lang.COMMAND_INFO_HEADER.getConfigValue(null));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_INFO_LINE_1.getConfigValue(new String[] { owner }));
        player.sendMessage(Lang.COMMAND_INFO_LINE_2.getConfigValue(new String[] { chunkCord }));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_INFO_FOOTER.getConfigValue(null));

        plugin.getUtility().renderChunkBorder(player, chunk, "info");
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
