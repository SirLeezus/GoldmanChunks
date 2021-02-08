package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.lists.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Trust extends SubCommand {

    @Override
    public String getName() {
        return "trust";
    }

    @Override
    public String getDescription() {
        return "Trust the player to the chunk you're standing on.";
    }

    @Override
    public String getSyntax() {
        return "/chunk trust &f<player>";
    }

    @Override
    public String getPermission() {
        return "mychunks.command.trust";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();

        if (args.length > 1) {

            Player target;

            if (plugin.getpU().getOnlinePlayers().contains(args[1])) {
                target = Bukkit.getPlayer(args[1]);
            } else {
                player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_PLAYER_NOT_ONLINE.getString(new String[]{ args[1] }));
                return;
            }

            Chunk chunk = player.getLocation().getChunk();
            String chunkCord = plugin.getpU().formatChunk(chunk);

            if (plugin.getSqLite().isChunkTrusted(chunkCord, target.getUniqueId())) {
                player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_TRUST_ALREADY_ADDED.getString(new String[] { target.getName(), chunkCord }));
                return;
            }

            if (plugin.getSqLite().isChunkClaimed(chunkCord)) {
                if (plugin.getSqLite().isChunkOwner(chunkCord, player.getUniqueId())) {
                    plugin.getSqLite().addChunkTrusted(chunkCord, target.getUniqueId());
                    player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_TRUST_ADDED_PLAYER.getString(new String[] { target.getName(), chunkCord }));
                } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_TRUST_NOT_OWNER.getString(null));
            } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_TRUST_NOT_OWNER.getString(null));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
