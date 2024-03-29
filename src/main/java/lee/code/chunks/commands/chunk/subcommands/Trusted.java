package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.RenderTypes;
import net.kyori.adventure.text.Component;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Trusted extends SubCommand {

    @Override
    public String getName() {
        return "trusted";
    }

    @Override
    public String getDescription() { return "Checks your global and chunk trusted players on the chunk you're standing on."; }

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
        Chunk chunk = player.getLocation().getChunk();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);
        CacheManager cacheManager = plugin.getCacheManager();

        if (cacheManager.isChunkClaimed(chunkCord)) {
            List<Component> lines = new ArrayList<>();
            Component spacer = Component.text("");

            UUID ownerUUID = cacheManager.getChunkOwnerUUID(chunkCord);
            String trusted = String.join(", ", cacheManager.getChunkTrustedNames(chunkCord));
            String globalTrusted = String.join(", ", cacheManager.getGlobalTrustedNames(ownerUUID));

            lines.add(Lang.COMMAND_TRUSTED_HEADER.getComponent(null));
            lines.add(spacer);
            lines.add(Lang.COMMAND_TRUSTED_LINE_1.getComponent(new String[] { globalTrusted }));
            lines.add(Lang.COMMAND_TRUSTED_LINE_2.getComponent(new String[] { trusted }));
            lines.add(spacer);
            lines.add(Lang.COMMAND_TRUSTED_FOOTER.getComponent(null));

            for (Component line : lines) player.sendMessage(line);
        } else if (cacheManager.isAdminChunk(chunkCord)) {
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_TRUSTED_ADMIN_CHUNK.getComponent(null)));
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_TRUSTED_NOT_CLAIMED.getComponent(null)));
        plugin.getPU().renderChunkBorder(player, chunk, RenderTypes.INFO);
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
