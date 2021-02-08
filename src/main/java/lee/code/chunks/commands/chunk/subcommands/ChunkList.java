package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.menusystem.menus.PlayerChunks;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChunkList extends SubCommand {

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Opens your chunks claimed menu.";
    }

    @Override
    public String getSyntax() {
        return "/chunk list";
    }

    @Override
    public String getPermission() {
        return "mychunks.command.list";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        new PlayerChunks(plugin.getData().getPlayerMenuUtil(player.getUniqueId())).open();
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
