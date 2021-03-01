package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.Values;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Scanner;
import java.util.UUID;

public class Sell extends SubCommand {

    @Override
    public String getName() {
        return "sell";
    }

    @Override
    public String getDescription() {
        return "Put the chunk you're standing on up for sale.";
    }

    @Override
    public String getSyntax() {
        return "/chunk sell &f<price>";
    }

    @Override
    public String getPermission() {
        return "chunk.command.sell";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        if (args.length > 1) {
            Scanner valueScanner = new Scanner(args[1]);
            if (valueScanner.hasNextInt()) {
                int value = Integer.parseInt(args[1]);
                if (value < Values.CHUNK_SELL_PRICE_MAX.getValue()) {
                    UUID uuid = player.getUniqueId();
                    Chunk chunk = player.getLocation().getChunk();
                    String chunkCord = plugin.getPU().formatChunkLocation(chunk);
                    if (!cache.isAdminChunk(chunkCord)) {
                        if (cache.isChunkClaimed(chunkCord)) {
                            if (cache.isChunkOwner(chunkCord, uuid)) {
                                cache.setChunkPrice(chunkCord, value);
                                player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_SELL_SUCCESSFUL.getString(new String[]{chunkCord, plugin.getPU().formatAmount(value)}));
                            } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_SELL_NOT_OWNER.getString(new String[]{cache.getChunkOwnerName(chunkCord)}));
                        } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_SELL_NOT_CLAIMED.getString(null));
                    } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_SELL_ADMIN_CHUNK.getString(null));
                } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_SELL_MAX_VALUE.getString(new String[]{String.valueOf(Values.CHUNK_SELL_PRICE_MAX.getValue())}));
            } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_SELL_NOT_NUMBER.getString(new String[]{args[1]}));
        } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_SELL_NO_NUMBER.getString(null));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
