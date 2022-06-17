package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.Lang;
import lee.code.core.util.bukkit.BukkitUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AbandonAllClaims extends SubCommand {

    @Override
    public String getName() {
        return "abandonallclaims";
    }

    @Override
    public String getDescription() {
        return "Unclaims all the chunks you own.";
    }

    @Override
    public String getSyntax() {
        return "/chunk abandonallclaims";
    }

    @Override
    public String getPermission() {
        return "chunk.command.abandonallclaims";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        UUID uuid = player.getUniqueId();

        if (cacheManager.hasClaimedChunks(uuid)) {
            cacheManager.unclaimAllChunks(uuid);
            int maxClaims = cacheManager.getPlayerMaxClaimAmount(uuid);
            int totalClaims = cacheManager.getClaimedAmount(uuid);
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ABANDONALLCLAIMS_SUCCESSFUL.getComponent(new String[] { BukkitUtils.parseValue(totalClaims), BukkitUtils.parseValue(maxClaims) })));
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ABANDONALLCLAIMS_NO_CLAIMS.getComponent(null)));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
