package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.RenderTypes;
import lee.code.chunks.lists.Settings;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SetPrice extends SubCommand {

    @Override
    public String getName() {
        return "setprice";
    }

    @Override
    public String getDescription() {
        return "Set the price of the chunk you're standing on.";
    }

    @Override
    public String getSyntax() {
        return "/chunk setprice &f<price>";
    }

    @Override
    public String getPermission() {
        return "chunk.command.setprice";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        PU pu = plugin.getPU();

        if (args.length > 1) {
            if (pu.containOnlyNumbers(args[1])) {
                long value = Long.parseLong(args[1]);
                if (value < Settings.CHUNK_SELL_PRICE_MAX.getValue()) {
                    UUID uuid = player.getUniqueId();
                    Chunk chunk = player.getLocation().getChunk();
                    String chunkCord = pu.formatChunkLocation(chunk);
                    if (!cache.isAdminChunk(chunkCord)) {
                        if (cache.isChunkClaimed(chunkCord)) {
                            if (cache.isChunkOwner(chunkCord, uuid)) {
                                cache.setChunkPrice(chunkCord, value);
                                if (value == 0) player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_SETPRICE_REMOVE_SUCCESSFUL.getComponent(new String[]{chunkCord})));
                                else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_SETPRICE_SUCCESSFUL.getComponent(new String[]{chunkCord, plugin.getPU().formatAmount(value)})));
                                pu.renderChunkBorder(player, chunk, RenderTypes.INFO);
                            } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_SETPRICE_NOT_OWNER.getComponent(new String[]{cache.getChunkOwnerName(chunkCord)})));
                        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_SETPRICE_NOT_CLAIMED.getComponent(null)));
                    } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_SETPRICE_ADMIN_CHUNK.getComponent(null)));
                } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_SETPRICE_MAX_VALUE.getComponent(new String[]{String.valueOf(Settings.CHUNK_SELL_PRICE_MAX.getValue())})));
            } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_SETPRICE_NOT_NUMBER.getComponent(new String[]{args[1]})));
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_SETPRICE_NO_NUMBER.getComponent(null)));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
