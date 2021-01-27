package lee.code.mychunks.commands.chunk.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.files.defaults.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reload extends SubCommand {

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "Reload the plugin configs.";
    }

    @Override
    public String getSyntax() {
        return "/chunk reload";
    }

    @Override
    public String getPermission() {
        return "mychunks.command.reload";
    }

    @Override
    public void perform(Player player, String[] args) {
        MyChunks plugin = MyChunks.getPlugin();
        plugin.reloadFiles();
        plugin.loadDefaults();
        player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.COMMAND_RELOAD_SUCCESSFUL.getConfigValue(null));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        MyChunks plugin = MyChunks.getPlugin();
        plugin.reloadFiles();
        plugin.loadDefaults();
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.COMMAND_RELOAD_SUCCESSFUL.getConfigValue(null));
    }
}
