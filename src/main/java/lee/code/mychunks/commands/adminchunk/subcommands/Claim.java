package lee.code.mychunks.commands.adminchunk.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.database.SQLite;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Claim extends SubCommand {

    @Override
    public String getName() {
        return "claim";
    }

    @Override
    public String getDescription() {
        return "Select a group of chunks to claim.";
    }

    @Override
    public String getSyntax() {
        return "/adminchunk claim";
    }

    @Override
    public String getPermission() {
        return "mychunks.admin";
    }

    @Override
    public void perform(Player player, String[] args) {

        MyChunks plugin = MyChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getUtility().formatChunk(chunk);

        if (!SQL.isChunkClaimed(chunkCord)) {

            Vector start = new Vector(chunk.getX(), 0, chunk.getZ());

            String selectedMessage = "Chunk selected: world %.0f %.0f";
            player.sendMessage(String.format(selectedMessage, start.getX(), start.getZ()));

            if(!plugin.getData().hasAdminClaimSelection(player.getUniqueId())) {
                plugin.getData().addAdminClaimSelection(player.getUniqueId(), start);
                //TODO send message to player pos 1 is selected
                return;
            }

            Vector stop = plugin.getData().getAdminClaimSelection(player.getUniqueId());
            Vector max = Vector.getMaximum(start, stop);
            Vector min = Vector.getMinimum(start, stop);

            for(double x = min.getX(); x <= max.getX(); x++) {
                for(double z = min.getZ(); z <= max.getZ(); z++) {
                    String inSelectionMessage = "Chunk in selection: world %.0f %.0f";
                    player.sendMessage(String.format(inSelectionMessage, x, z));
                }
            }
            plugin.getData().removeAdminClaimSelection(player.getUniqueId());
            //TODO send message chunks have been claimed.
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {

    }
}
