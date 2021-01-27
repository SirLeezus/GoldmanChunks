package lee.code.mychunks.commands.adminchunk.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.database.SQLite;
import lee.code.mychunks.files.defaults.Lang;
import lee.code.mychunks.menusystem.menus.AdminChunkSettings;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Manage extends SubCommand {

    @Override
    public String getName() {
        return "manage";
    }

    @Override
    public String getDescription() {
        return "Change the settings of the admin chunk you are standing on.";
    }

    @Override
    public String getSyntax() {
        return "/adminchunk manage";
    }

    @Override
    public String getPermission() {
        return "mychunk.admin";
    }

    @Override
    public void perform(Player player, String[] args) {
        MyChunks plugin = MyChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getUtility().formatChunk(chunk);
        if (SQL.isAdminChunk(chunkCord)) {
            new AdminChunkSettings(plugin.getData().getPlayerMenuUtil(player.getUniqueId())).open();
        } else player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_COMMAND_ADMIN_MANAGE_NOT_ADMIN_CHUNK.getConfigValue(null));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
