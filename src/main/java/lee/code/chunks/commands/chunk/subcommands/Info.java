package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.RenderTypes;
import lee.code.core.util.bukkit.BukkitUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Info extends SubCommand {

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getDescription() {
        return "Displays info about the chunk you're standing on.";
    }

    @Override
    public String getSyntax() {
        return "/chunk info";
    }

    @Override
    public String getPermission() {
        return "chunk.command.info";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        PU pu = plugin.getPU();

        String owner = "";
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = pu.serializeChunkLocation(chunk);
        if (cacheManager.isAdminChunk(chunkCord)) owner = "&4&lAdmin";
        else if (cacheManager.isChunkClaimed(chunkCord)) owner = cacheManager.getChunkOwnerName(chunkCord);

        List<Component> lines = new ArrayList<>();
        Component spacer = Component.text("");
        lines.add(Lang.COMMAND_INFO_HEADER.getComponent(null));
        lines.add(spacer);
        lines.add(Lang.COMMAND_INFO_LINE_1.getComponent(new String[] { owner }));
        lines.add(Lang.COMMAND_INFO_LINE_2.getComponent(new String[] { chunkCord }));
        if (cacheManager.isChunkClaimed(chunkCord)) if (cacheManager.isChunkForSale(chunkCord)) lines.add(Lang.COMMAND_INFO_LINE_3.getComponent(new String[] { BukkitUtils.parseValue(cacheManager.getChunkPrice(chunkCord)) }));
        lines.add(spacer);
        lines.add(Lang.COMMAND_INFO_FOOTER.getComponent(null));

        for (Component line : lines) player.sendMessage(line);
        pu.renderChunkBorder(player, chunk, RenderTypes.INFO);
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
