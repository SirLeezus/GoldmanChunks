package lee.code.mychunks.commands.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.files.defaults.Lang;
import lee.code.mychunks.menusystem.menus.PlayerChunks;
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
        MyChunks plugin = MyChunks.getPlugin();
        new PlayerChunks(plugin.getData().getPlayerMenuUtil(player.getUniqueId())).open();
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
