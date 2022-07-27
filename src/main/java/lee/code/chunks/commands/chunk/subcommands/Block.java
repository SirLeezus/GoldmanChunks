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

public class Block extends SubCommand {

    @Override
    public String getName() {
        return "block";
    }

    @Override
    public String getDescription() {
        return "Block a player from entering or teleporting to your chunks.";
    }

    @Override
    public String getSyntax() {
        return "/chunk block &f<player>";
    }

    @Override
    public String getPermission() {
        return "chunk.command.block";
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
                if (!uuid.equals(targetUUID)) {
                    if (!cacheManager.isBlocked(uuid, targetUUID)) {
                        if (target.isOnline()) {
                            Player targetPlayer = target.getPlayer();
                            if (targetPlayer != null){
                                String targetChunk = plugin.getPU().serializeChunkLocation(targetPlayer.getLocation().getChunk());
                                if (cacheManager.isChunkClaimed(targetChunk)) {
                                    if (cacheManager.getChunkOwnerUUID(targetChunk).equals(uuid)) {
                                        targetPlayer.teleportAsync(plugin.getEssentialsAPI().getSpawn()).thenAccept(result -> {
                                            targetPlayer.sendActionBar(Lang.TELEPORT.getComponent(null));
                                            targetPlayer.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BLOCK_ADDED_ON_CHUNK.getComponent(new String[] { player.getName() })));
                                        });
                                    }
                                }
                            }
                        }
                        cacheManager.addBlockedUser(uuid, targetUUID);
                        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_BLOCK_ADDED_PLAYER.getComponent(new String[] { target.getName() })));
                    } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_BLOCK_ALREADY_BLOCKED.getComponent(new String[] { target.getName() })));
                } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_BLOCK_SELF.getComponent(null)));
            } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[]{args[1]})));
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_BLOCK_ADD_NO_TARGET_PLAYER.getComponent(null)));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
