package lee.code.chunks.commands.adminchunk.subcommands;

import lee.code.chunks.Data;
import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.chunksettings.ChunkAdminSettings;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.RenderTypes;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Selection extends SubCommand {

    @Override
    public String getName() {
        return "selection";
    }

    @Override
    public String getDescription() {
        return "Select a group of chunks.";
    }

    @Override
    public String getSyntax() {
        return "/adminchunk selection &f<set/claim/unclaim/clear> <setting> <value>";
    }

    @Override
    public String getPermission() {
        return "mychunks.admin.selection";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        Cache cache = plugin.getCache();
        PU pu = plugin.getPU();

        UUID uuid = player.getUniqueId();
        Chunk chunk = player.getLocation().getChunk();

        if (args.length < 2) {
            Vector start = new Vector(chunk.getX(), 0, chunk.getZ());
            String world = player.getWorld().getName();
            String selectedChunk = world + ",%.0f,%.0f";
            pu.renderChunkBorder(player, chunk, RenderTypes.CLAIM);

            if (!data.hasFirstAdminSelection(uuid)) {
                data.setFirstAdminSelection(uuid, start);
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_SELECTION_FIRST.getComponent(new String[] { String.format(selectedChunk, start.getX(), start.getZ()) })));
                return;
            }

            Vector stop = plugin.getData().getFistAdminSelection(player.getUniqueId());
            Vector max = Vector.getMaximum(start, stop);
            Vector min = Vector.getMinimum(start, stop);

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                List<String> chunks = new ArrayList<>();
                int selected = 0;
                for (double x = min.getX(); x <= max.getX(); x++) {
                    for(double z = min.getZ(); z <= max.getZ(); z++) {
                        String inSelectionMessage = world + ",%.0f,%.0f";
                        String chunkCordSelected = String.format(inSelectionMessage, x, z);
                        if (!cache.isChunkClaimed(chunkCordSelected)) {
                            selected++;
                            chunks.add(chunkCordSelected);
                        }
                    }
                }
                data.setAdminSelectedChunks(uuid, chunks);
                data.removeFirstAdminSelection(uuid);
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_SELECTION_SUCCESSFUL.getComponent(new String[] { String.valueOf(selected) } )));
            });
        } else {
            String command = args[1].toLowerCase();

            switch (command) {

                case "clear" -> {
                    data.removeAdminSelectedChunks(uuid);
                    data.removeFirstAdminSelection(uuid);
                    player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_SELECTION_CLEAR_SUCCESSFUL.getComponent(null)));
                }

                case "set" -> {
                    if (args.length == 4) {
                        if (data.hasAdminSelectedChunks(uuid)) {
                            String setting = args[2].toUpperCase();
                            boolean value = Boolean.parseBoolean(args[3]);
                            String settingFormat = value ? Lang.TRUE.getString() : Lang.FALSE.getString();

                            if (pu.getAdminChunkSettings().contains(setting)) {
                                int updated = cache.updateBulkAdminChunks(data.getAdminSelectedChunks(uuid), ChunkAdminSettings.valueOf(setting), value);
                                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_SELECTION_SETTING_UPDATE.getComponent(new String[]{setting, settingFormat, String.valueOf(updated)})));
                            }
                        }
                    } else player.sendMessage(Lang.USAGE.getComponent(new String[] { getSyntax() }));
                }

                case "claim" -> {
                    if (data.hasAdminSelectedChunks(uuid)) {
                        cache.claimBulkAdminChunks(data.getAdminSelectedChunks(uuid));
                        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_CLAIM_SELECTION.getComponent(new String[] { String.valueOf(data.getAdminSelectedChunks(uuid).size()) })));
                    } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_SELECTION_NO_CHUNKS_SELECTED.getComponent(null)));
                }

                case "unclaim" -> {
                    if (data.hasAdminSelectedChunks(uuid)) {
                        cache.unclaimBulkAdminChunk(data.getAdminSelectedChunks(uuid));
                        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_UNCLAIM_SELECTION.getComponent(new String[] { String.valueOf(data.getAdminSelectedChunks(uuid).size()) })));
                    } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_SELECTION_NO_CHUNKS_SELECTED.getComponent(null)));
                }
            }
        }
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
