package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.Settings;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Scanner;
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

        if (args.length > 1) {
            Scanner valueScanner = new Scanner(args[1]);
            if (valueScanner.hasNextInt()) {
                int value = Integer.parseInt(args[1]);
                if (value < Settings.CHUNK_SELL_PRICE_MAX.getValue()) {
                    UUID uuid = player.getUniqueId();
                    Chunk chunk = player.getLocation().getChunk();
                    String chunkCord = plugin.getPU().formatChunkLocation(chunk);
                    if (!cache.isAdminChunk(chunkCord)) {
                        if (cache.isChunkClaimed(chunkCord)) {
                            if (cache.isChunkOwner(chunkCord, uuid)) {
                                cache.setChunkPrice(chunkCord, value);
                                if (value == 0) player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_SETPRICE_REMOVE_SUCCESSFUL.getString(new String[]{chunkCord}));
                                else player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_SETPRICE_SUCCESSFUL.getString(new String[]{chunkCord, plugin.getPU().formatAmount(value)}));
                                plugin.getPU().renderChunkBorder(player, chunk, "info");
                            } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_SETPRICE_NOT_OWNER.getString(new String[]{cache.getChunkOwnerName(chunkCord)}));
                        } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_SETPRICE_NOT_CLAIMED.getString(null));
                    } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_SETPRICE_ADMIN_CHUNK.getString(null));
                } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_SETPRICE_MAX_VALUE.getString(new String[]{String.valueOf(Settings.CHUNK_SELL_PRICE_MAX.getValue())}));
            } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_SETPRICE_NOT_NUMBER.getString(new String[]{args[1]}));
        } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_SETPRICE_NO_NUMBER.getString(null));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
