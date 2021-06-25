package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
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
        return "Unclaim all the chunks you own.";
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
        Cache cache = plugin.getCache();
        UUID uuid = player.getUniqueId();

        if (cache.hasClaimedChunks(uuid)) {
            cache.unclaimAllChunks(uuid);
            int maxClaims = cache.getPlayerMaxClaimAmount(uuid);
            int totalClaims = cache.getClaimedAmount(uuid);
            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ABANDONALLCLAIMS_SUCCESSFUL.getComponent(new String[] { plugin.getPU().formatAmount(totalClaims), plugin.getPU().formatAmount(maxClaims) })));
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ABANDONALLCLAIMS_NO_CLAIMS.getComponent(null)));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
