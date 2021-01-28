package lee.code.mychunks.commands.adminchunk.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.files.defaults.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Bypass extends SubCommand {

    @Override
    public String getName() {
        return "bypass";
    }

    @Override
    public String getDescription() {
        return "Bypass all claimed chunks.";
    }

    @Override
    public String getSyntax() {
        return "/adminchunk bypass";
    }

    @Override
    public String getPermission() {
        return "mychunks.admin.bypass";
    }

    @Override
    public void perform(Player player, String[] args) {
        UUID uuid = player.getUniqueId();
        MyChunks plugin = MyChunks.getPlugin();

        if (plugin.getData().hasAdminBypass(uuid)) {
            plugin.getData().removeAdminBypass(uuid);
            player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.COMMAND_ADMIN_BYPASS_DISABLED.getConfigValue(null));
        } else {
            plugin.getData().addAdminBypass(uuid);
            player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.COMMAND_ADMIN_BYPASS_ENABLED.getConfigValue(null));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
