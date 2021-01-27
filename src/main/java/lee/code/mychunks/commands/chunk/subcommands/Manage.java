package lee.code.mychunks.commands.chunk.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.files.defaults.Lang;
import lee.code.mychunks.menusystem.menus.ChunkManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Manage extends SubCommand {

    @Override
    public String getName() {
        return "manage";
    }

    @Override
    public String getDescription() {
        return "Opens your trusted and chunk manager menu.";
    }

    @Override
    public String getSyntax() {
        return "/chunk manage";
    }

    @Override
    public String getPermission() {
        return "mychunks.command.manage";
    }

    @Override
    public void perform(Player player, String[] args) {
        MyChunks plugin = MyChunks.getPlugin();
        new ChunkManager(plugin.getData().getPlayerMenuUtil(player.getUniqueId())).open();
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
