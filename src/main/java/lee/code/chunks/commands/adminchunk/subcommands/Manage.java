package lee.code.chunks.commands.adminchunk.subcommands;

import lee.code.chunks.Data;
import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.menusystem.menus.AdminChunkSettings;
import org.bukkit.Chunk;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Manage extends SubCommand {

    @Override
    public String getName() {
        return "manage";
    }

    @Override
    public String getDescription() {
        return "Opens a menu to manage admin chunk settings.";
    }

    @Override
    public String getSyntax() {
        return "/adminchunk manage";
    }

    @Override
    public String getPermission() {
        return "mychunk.admin.manage";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        Data data = plugin.getData();
        PU pu = plugin.getPU();

        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = pu.serializeChunkLocation(chunk);
        if (cacheManager.isAdminChunk(chunkCord)) {
            new AdminChunkSettings(data.getPlayerMU(player.getUniqueId())).open();
            player.playSound(player.getLocation(), Sound.ENTITY_LLAMA_SWAG, 1, 1);
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ADMIN_MANAGE_NOT_ADMIN_CHUNK.getComponent(null)));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
