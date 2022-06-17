package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.Lang;
import lee.code.core.util.bukkit.BukkitUtils;
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
        CacheManager cacheManager = plugin.getCacheManager();
        PU pu = plugin.getPU();

        List<Component> chunkMap = new ArrayList<>();
        List<Component> chunkSquare = new ArrayList<>();

        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = pu.serializeChunkLocation(chunk);

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
                        chunkSquare.add(BukkitUtils.parseColorComponent("&9■").hoverEvent(BukkitUtils.parseColorComponent("&b" + chunkSelected)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk teleport " + chunkSelected)));
                    } else if (cacheManager.isAdminChunk(chunkSelected)) {
                        chunkSquare.add(BukkitUtils.parseColorComponent("&4■").hoverEvent(BukkitUtils.parseColorComponent("&b" + chunkSelected)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk teleport " + chunkSelected)));
                    } else if (cacheManager.isChunkClaimed(chunkSelected)) {
                        UUID owner = cacheManager.getChunkOwnerUUID(chunkSelected);
                        if (cacheManager.isChunkOwner(chunkSelected, uuid)) chunkSquare.add(BukkitUtils.parseColorComponent("&2■").hoverEvent(BukkitUtils.parseColorComponent("&b" + chunkSelected)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk teleport " + chunkSelected)));
                        else if (cacheManager.isChunkTrusted(chunkSelected, uuid)) chunkSquare.add(BukkitUtils.parseColorComponent("&a■").hoverEvent(BukkitUtils.parseColorComponent("&b" + chunkSelected)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk teleport " + chunkSelected)));
                        else if (cacheManager.isGlobalTrusted(owner, uuid)) chunkSquare.add(BukkitUtils.parseColorComponent("&a■").hoverEvent(BukkitUtils.parseColorComponent("&b" + chunkSelected)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk teleport " + chunkSelected)));
                        else chunkSquare.add(BukkitUtils.parseColorComponent("&c■").hoverEvent(BukkitUtils.parseColorComponent("&b" + chunkSelected)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk teleport " + chunkSelected)));
                    } else chunkSquare.add(BukkitUtils.parseColorComponent("&7■").hoverEvent(BukkitUtils.parseColorComponent("&b" + chunkSelected)).clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/chunk teleport " + chunkSelected)));
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
            
            String line1 = BukkitUtils.parseColorString(" &e\\ &9&lN &e/ ");
            String line2 = BukkitUtils.parseColorString(" &b&lW &6&l• &b&lE");
            String line3 = BukkitUtils.parseColorString(" &e/ &b&lS &e\\");

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
