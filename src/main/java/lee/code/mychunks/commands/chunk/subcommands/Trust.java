package lee.code.mychunks.commands.chunk.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.files.defaults.Lang;
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
        MyChunks plugin = MyChunks.getPlugin();

        if (args.length > 1) {

            Player target;

            if (plugin.getUtility().getOnlinePlayers().contains(args[1])) {
                target = Bukkit.getPlayer(args[1]);
            } else {
                player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_PLAYER_NOT_ONLINE.getConfigValue(new String[]{ args[1] }));
                return;
            }

            Chunk chunk = player.getLocation().getChunk();
            String chunkCord = plugin.getUtility().formatChunk(chunk);

            if (plugin.getSqLite().isChunkTrusted(chunkCord, target.getUniqueId())) {
                player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_COMMAND_TRUST_ALREADY_ADDED.getConfigValue(new String[] { target.getName(), chunkCord }));
                return;
            }

            if (plugin.getSqLite().isChunkClaimed(chunkCord)) {
                if (plugin.getSqLite().isChunkOwner(chunkCord, player.getUniqueId())) {
                    plugin.getSqLite().addChunkTrusted(chunkCord, target.getUniqueId());
                    player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.COMMAND_TRUST_ADDED_PLAYER.getConfigValue(new String[] { target.getName(), chunkCord }));
                } else player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_COMMAND_TRUST_NOT_OWNER.getConfigValue(null));
            } else player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_COMMAND_TRUST_NOT_OWNER.getConfigValue(null));
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
