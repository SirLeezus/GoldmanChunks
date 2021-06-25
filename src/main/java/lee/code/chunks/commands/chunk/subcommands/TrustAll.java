package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TrustAll extends SubCommand {

    @Override
    public String getName() {
        return "trustall";
    }

    @Override
    public String getDescription() {
        return "Add a player to your global trust list.";
    }

    @Override
    public String getSyntax() {
        return "/chunk trustall &f<player>";
    }

    @Override
    public String getPermission() {
        return "chunk.command.trustall";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        if (args.length > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[1]);
            if (target != null) {
                UUID targetUUID = target.getUniqueId();
                if (!cache.isGlobalTrusted(player.getUniqueId(), targetUUID)) {
                    cache.addGlobalTrusted(player.getUniqueId(), targetUUID);
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_TRUSTALL_ADDED_PLAYER.getComponent(new String[] { target.getName() })));
                } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_TRUSTALL_ALREADY_ADDED.getComponent(new String[] { target.getName() })));
            } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[]{args[1]})));
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_TRUSTALL_NO_TARGET_PLAYER.getComponent(null)));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
