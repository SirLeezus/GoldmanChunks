package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.Data;
import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
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
        return "Able to fly in claimed or trusted chunks.";
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
        Cache cache = plugin.getCache();
        UUID uuid = player.getUniqueId();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (cache.isChunkClaimed(chunkCord)) {
            if (cache.isChunkOwner(chunkCord, uuid) || cache.isChunkTrusted(chunkCord, uuid)) {
                if (!cache.isChunkFlying(uuid)) {
                    cache.setChunkFlying(uuid, true);
                    player.setAllowFlight(true);
                    player.setFlying(true);
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_FLY_TOGGLE_SUCCESSFUL.getComponent(new String[] { Lang.ON.getString() })));
                } else {
                    cache.setChunkFlying(uuid, false);
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
