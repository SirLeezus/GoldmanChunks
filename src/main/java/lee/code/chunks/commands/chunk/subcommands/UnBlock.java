package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.Lang;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnBlock extends SubCommand {

    @Override
    public String getName() {
        return "unblock";
    }

    @Override
    public String getDescription() {
        return "Remove a player from your blocked list.";
    }

    @Override
    public String getSyntax() {
        return "/chunk unblock &f<player>";
    }

    @Override
    public String getPermission() {
        return "chunk.command.unblock";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();

        if (args.length > 1) {
            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[1]);
            if (target != null) {
                UUID uuid = player.getUniqueId();
                UUID targetUUID = target.getUniqueId();
                if (cacheManager.isBlocked(uuid, targetUUID)) {
                    cacheManager.removeBlockedUser(uuid, targetUUID);
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BLOCK_REMOVED_PLAYER.getComponent(new String[] { target.getName() })));
                } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_BLOCK_NOT_BLOCKED.getComponent(new String[] { target.getName() })));
            } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[]{args[1]})));
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_BLOCK_REMOVE_NO_TARGET_PLAYER.getComponent(null)));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
