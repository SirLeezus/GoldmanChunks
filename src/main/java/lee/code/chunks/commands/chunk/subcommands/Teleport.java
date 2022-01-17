package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Teleport extends SubCommand {

    @Override
    public String getName() {
        return "teleport";
    }

    @Override
    public String getDescription() {
        return "Teleports you to a chunk you own.";
    }

    @Override
    public String getSyntax() {
        return "/chunk teleport &f<chunk>";
    }

    @Override
    public String getPermission() {
        return "chunk.command.teleport";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        PU pu = plugin.getPU();

        if (args.length > 1) {
            UUID uuid = player.getUniqueId();
            String chunk = args[1].trim();
            if (!cache.isAdminChunk(chunk)) {
                if (cache.isChunkClaimed(chunk)) {
                    if (cache.isChunkOwner(chunk, uuid)) {
                        Location chunkLocation = pu.unFormatChunkLocation(chunk);
                        pu.teleportPlayerToChunk(player, chunkLocation);
                    } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_TELEPORT_NOT_OWNER.getComponent(new String[] { chunk })));
                } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_TELEPORT_NOT_CLAIMED.getComponent(new String[] { chunk })));
            } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_TELEPORT_NOT_OWNER.getComponent(new String[] { chunk })));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
