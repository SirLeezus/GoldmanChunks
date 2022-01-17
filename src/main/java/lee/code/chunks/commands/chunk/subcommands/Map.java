package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Map extends SubCommand {

    @Override
    public String getName() {
        return "map";
    }

    @Override
    public String getDescription() {
        return "Displays a map of chunks around you.";
    }

    @Override
    public String getSyntax() {
        return "/chunk map";
    }

    @Override
    public String getPermission() {
        return "chunk.command.map";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        UUID uuid = player.getUniqueId();
        Cache cache = plugin.getCache();
        PU pu = plugin.getPU();

        List<Component> chunkMap = new ArrayList<>();
        List<Component> chunkSquare = new ArrayList<>();

        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = pu.formatChunkLocation(chunk);

        chunkMap.add(Lang.COMMAND_MAP_HEADER.getComponent(null));

        String world = chunk.getWorld().getName();

        Location location = player.getLocation();
        player.teleportAsync(new Location(player.getWorld(),location.getX(), location.getY(), location.getZ(), (float) 180, (float) 0), PlayerTeleportEvent.TeleportCause.UNKNOWN);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {

            int firstX = chunk.getX() - 9;
            int x = chunk.getX() - 9;
            int z = chunk.getZ() - 5;

            for (int l = 1; l <= 11; l++) {
                for (int w = 1; w <= 19; w++) {

                    String chunkSelected = world + "," + x + "," + z;

                    if ((chunkSelected).equals(chunkCord)) {
                        chunkSquare.add(pu.formatC("&9■").hoverEvent(pu.formatC("&b" + chunkSelected)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk teleport " + chunkSelected)));
                    } else if (cache.isAdminChunk(chunkSelected)) {
                        chunkSquare.add(pu.formatC("&4■").hoverEvent(pu.formatC("&b" + chunkSelected)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk teleport " + chunkSelected)));
                    } else if (cache.isChunkClaimed(chunkSelected)) {
                        UUID owner = cache.getChunkOwnerUUID(chunkSelected);

                        if (cache.isChunkOwner(chunkSelected, uuid)) chunkSquare.add(pu.formatC("&2■").hoverEvent(pu.formatC("&b" + chunkSelected)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk teleport " + chunkSelected)));
                        else if (cache.isChunkTrusted(chunkSelected, uuid)) chunkSquare.add(pu.formatC("&a■").hoverEvent(pu.formatC("&b" + chunkSelected)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk teleport " + chunkSelected)));
                        else if (cache.isGlobalTrusted(owner, uuid)) chunkSquare.add(pu.formatC("&a■").hoverEvent(pu.formatC("&b" + chunkSelected)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk teleport " + chunkSelected)));
                        else chunkSquare.add(pu.formatC("&c■").hoverEvent(pu.formatC("&b" + chunkSelected)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk teleport " + chunkSelected)));

                    } else chunkSquare.add(pu.formatC("&7■").hoverEvent(pu.formatC("&b" + chunkSelected)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk teleport " + chunkSelected)));
                    x++;
                }
                x = firstX;
                z++;

                Component output = Component.text(" ");
                for (Component square : chunkSquare) output = output.append(square).append(Component.text(" "));

                chunkMap.add(output);
                chunkSquare.clear();
            }
            chunkMap.add(Lang.COMMAND_MAP_FOOTER.getComponent(null));

            String line1 = pu.format(" &e\\ &9&lN &e/ ");
            String line2 = pu.format(" &b&lW &6&l• &b&lE");
            String line3 = pu.format(" &e/ &b&lS &e\\");

            List<Component> lines = new ArrayList<>();
            Component spacer = Component.text("");

            lines.add(Lang.COMMAND_MAP_KEY_HEADER.getComponent(null));
            lines.add(spacer);
            lines.add(Lang.COMMAND_MAP_LINE_1.getComponent(new String[] { line1 }));
            lines.add(Lang.COMMAND_MAP_LINE_2.getComponent(new String[] { line2 }));
            lines.add(Lang.COMMAND_MAP_LINE_3.getComponent(new String[] { line3 }));
            lines.add(spacer);

            for (Component line : lines) player.sendMessage(line);
            for (Component selectedChunk : chunkMap) player.sendMessage(selectedChunk);
        });
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
