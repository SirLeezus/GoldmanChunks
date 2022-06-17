package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.Lang;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Fly extends SubCommand {

    @Override
    public String getName() {
        return "fly";
    }

    @Override
    public String getDescription() {
        return "Toggles fly in claimed or trusted chunks.";
    }

    @Override
    public String getSyntax() {
        return "/chunk fly";
    }

    @Override
    public String getPermission() {
        return "chunk.command.fly";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();

        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);
        UUID uuid = player.getUniqueId();

        if (cacheManager.isChunkClaimed(chunkCord)) {
            UUID ownerUUID = cacheManager.getChunkOwnerUUID(chunkCord);
            if (cacheManager.isChunkOwner(chunkCord, uuid) || cacheManager.isChunkTrusted(chunkCord, uuid) || cacheManager.isGlobalTrusted(ownerUUID, uuid)) {
                if (!cacheManager.isChunkFlying(uuid)) {
                    cacheManager.setChunkFlying(uuid, true);
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_FLY_TOGGLE_SUCCESSFUL.getComponent(new String[] { Lang.ON.getString() })));
                } else {
                    cacheManager.setChunkFlying(uuid, false);
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_FLY_TOGGLE_SUCCESSFUL.getComponent(new String[] { Lang.OFF.getString() })));
                }
            } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_FLY_NOT_OWNER.getComponent(null)));
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_FLY_NOT_OWNER.getComponent(null)));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
