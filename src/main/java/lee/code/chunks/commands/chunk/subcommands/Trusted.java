package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.lists.Lang;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Trusted extends SubCommand {

    @Override
    public String getName() {
        return "trusted";
    }

    @Override
    public String getDescription() {
        return "Check your global and chunk trusted players.";
    }

    @Override
    public String getSyntax() {
        return "/chunk trusted";
    }

    @Override
    public String getPermission() {
        return "chunk.command.trusted";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        UUID uuid = player.getUniqueId();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunk(chunk);

        String trusted;
        if (plugin.getSqLite().isChunkOwner(chunkCord, uuid)) trusted = String.join(", ", plugin.getSqLite().getTrustedToChunk(chunkCord));
        else trusted = Lang.ERROR_COMMAND_TRUSTED_NOT_CHUNK_OWNER.getString(null);

        String globalTrusted = String.join(", ", plugin.getSqLite().getGlobalTrustedPlayers(uuid));

        player.sendMessage(Lang.COMMAND_TRUSTED_HEADER.getString(null));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_TRUSTED_LINE_1.getString(new String[] { globalTrusted }));
        player.sendMessage(Lang.COMMAND_TRUSTED_LINE_2.getString(new String[] { trusted }));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_TRUSTED_FOOTER.getString(null));

        plugin.getPU().renderChunkBorder(player, chunk, "info");
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
