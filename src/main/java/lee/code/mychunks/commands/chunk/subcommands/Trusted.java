package lee.code.mychunks.commands.chunk.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.files.defaults.Lang;
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
        return "mychunks.command.trusted";
    }

    @Override
    public void perform(Player player, String[] args) {
        MyChunks plugin = MyChunks.getPlugin();
        UUID uuid = player.getUniqueId();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getUtility().formatChunk(chunk);

        String trusted;
        if (plugin.getSqLite().isChunkOwner(chunkCord, uuid)) trusted = String.join(", ", plugin.getSqLite().getTrustedToChunk(chunkCord));
        else trusted = Lang.ERROR_COMMAND_TRUSTED_NOT_CHUNK_OWNER.getConfigValue(null);

        String globalTrusted = String.join(", ", plugin.getSqLite().getGlobalTrustedPlayers(uuid));

        player.sendMessage(Lang.COMMAND_TRUSTED_HEADER.getConfigValue(null));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_TRUSTED_LINE_1.getConfigValue(new String[] { globalTrusted }));
        player.sendMessage(Lang.COMMAND_TRUSTED_LINE_2.getConfigValue(new String[] { trusted }));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_TRUSTED_FOOTER.getConfigValue(null));

        plugin.getUtility().renderChunkBorder(player, chunk, "info");
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
