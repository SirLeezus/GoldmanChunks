package lee.code.mychunks.commands.chunk.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.files.defaults.Lang;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AutoClaim extends SubCommand {

    @Override
    public String getName() {
        return "autoclaim";
    }

    @Override
    public String getDescription() {
        return "Toggle auto claim to claim chunks as you walk.";
    }

    @Override
    public String getSyntax() {
        return "/chunk autoclaim";
    }

    @Override
    public String getPermission() {
        return "mychunks.command.autoclaim";
    }

    @Override
    public void perform(Player player, String[] args) {
        MyChunks plugin = MyChunks.getPlugin();

        UUID uuid = player.getUniqueId();
        Chunk chunk = player.getLocation().getChunk();

        if (plugin.getData().isPlayerAutoClaiming(uuid)) {
            plugin.getData().removePlayerAutoClaim(uuid);
            player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.COMMAND_AUTO_CLAIM_DISABLED.getConfigValue(null));
        } else {
            plugin.getData().setPlayerAutoClaim(uuid, chunk);
            player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.COMMAND_AUTO_CLAIM_ENABLED.getConfigValue(null));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
