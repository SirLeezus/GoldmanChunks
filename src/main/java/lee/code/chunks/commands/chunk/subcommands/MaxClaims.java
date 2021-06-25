package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.Settings;
import net.kyori.adventure.text.Component;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MaxClaims extends SubCommand {

    @Override
    public String getName() {
        return "maxclaims";
    }

    @Override
    public String getDescription() {
        return "Display info in chat regarding claims.";
    }

    @Override
    public String getSyntax() {
        return "/chunk maxclaims";
    }

    @Override
    public String getPermission() {
        return "chunk.command.maxclaims";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        UUID uuid = player.getUniqueId();

        int defaultClaims = cache.getPlayerDefaultClaimAmount(player);
        int claimed = cache.getClaimedAmount(uuid);
        int bonusClaims = cache.getBonusClaimsAmount(uuid);
        int accruedClaims = cache.getAccruedClaimsAmount(uuid);
        int maxClaims = cache.getPlayerMaxClaimAmount(uuid);
        long time = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        String timePlayed = plugin.getPU().formatSeconds(time);
        String timeRequired = plugin.getPU().formatSeconds(Settings.ACCRUED_CLAIMS_BASE_TIME_REQUIRED.getValue());
        int givenAmount = Settings.ACCRUED_CLAIMS_AMOUNT_GIVEN.getValue();

        List<Component> lines = new ArrayList<>();
        lines.add(Lang.COMMAND_MAX_CLAIMS_HEADER.getComponent(null));
        lines.add(Component.text(""));
        lines.add(Lang.COMMAND_MAX_CLAIMS_LINE_1.getComponent(new String[] { plugin.getPU().formatAmount(defaultClaims) }));
        lines.add(Lang.COMMAND_MAX_CLAIMS_LINE_2.getComponent(new String[] { plugin.getPU().formatAmount(bonusClaims) }));
        lines.add(Lang.COMMAND_MAX_CLAIMS_LINE_3.getComponent(new String[] { plugin.getPU().formatAmount(claimed) }));
        lines.add(Component.text(""));
        lines.add(Lang.COMMAND_MAX_CLAIMS_LINE_4.getComponent(new String[] { timePlayed }));
        lines.add(Lang.COMMAND_MAX_CLAIMS_LINE_5.getComponent(new String[] { plugin.getPU().formatAmount(givenAmount), timeRequired }));
        lines.add(Lang.COMMAND_MAX_CLAIMS_LINE_6.getComponent(new String[] { plugin.getPU().formatAmount(accruedClaims), plugin.getPU().formatAmount(Settings.ACCRUED_CLAIMS_MAX.getValue()) }));
        lines.add(Component.text(""));
        lines.add(Lang.COMMAND_MAX_CLAIMS_LINE_7.getComponent(new String[] { plugin.getPU().formatAmount(claimed), plugin.getPU().formatAmount(maxClaims) }));
        lines.add(Component.text(""));
        lines.add(Lang.COMMAND_MAX_CLAIMS_FOOTER.getComponent(null));

        for (Component line : lines) player.sendMessage(line);
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
