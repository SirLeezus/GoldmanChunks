package lee.code.chunks.commands.adminchunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.lists.Lang;
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
        GoldmanChunks plugin = GoldmanChunks.getPlugin();

        if (plugin.getData().hasAdminBypass(uuid)) {
            plugin.getData().removeAdminBypass(uuid);
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_BYPASS_DISABLED.getComponent(null)));
        } else {
            plugin.getData().addAdminBypass(uuid);
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_BYPASS_ENABLED.getComponent(null)));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
