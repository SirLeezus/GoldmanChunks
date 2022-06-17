package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.Settings;
import lee.code.core.util.bukkit.BukkitUtils;
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
        return "Displays info regarding max claims.";
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
        CacheManager cacheManager = plugin.getCacheManager();
        UUID uuid = player.getUniqueId();

        int defaultClaims = cacheManager.getPlayerDefaultClaimAmount(uuid);
        int claimed = cacheManager.getClaimedAmount(uuid);
        int bonusClaims = cacheManager.getBonusClaimsAmount(uuid);
        int accruedClaims = cacheManager.getAccruedClaimsAmount(uuid);
        int maxClaims = cacheManager.getPlayerMaxClaimAmount(uuid);
        long time = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        String timePlayed = BukkitUtils.parseSeconds(time);
        String timeRequired = BukkitUtils.parseSeconds(Settings.ACCRUED_CLAIMS_BASE_TIME_REQUIRED.getValue());
        int givenAmount = Settings.ACCRUED_CLAIMS_AMOUNT_GIVEN.getValue();
        Component spacer = Component.text("");

        List<Component> lines = new ArrayList<>();
        lines.add(Lang.COMMAND_MAX_CLAIMS_HEADER.getComponent(null));
        lines.add(spacer);
        lines.add(Lang.COMMAND_MAX_CLAIMS_LINE_1.getComponent(new String[] { BukkitUtils.parseValue(defaultClaims) }));
        lines.add(Lang.COMMAND_MAX_CLAIMS_LINE_2.getComponent(new String[] { BukkitUtils.parseValue(bonusClaims) }));
        lines.add(Lang.COMMAND_MAX_CLAIMS_LINE_3.getComponent(new String[] { BukkitUtils.parseValue(claimed) }));
        lines.add(spacer);
        lines.add(Lang.COMMAND_MAX_CLAIMS_LINE_4.getComponent(new String[] { timePlayed }));
        lines.add(Lang.COMMAND_MAX_CLAIMS_LINE_5.getComponent(new String[] { BukkitUtils.parseValue(givenAmount), timeRequired }));
        lines.add(Lang.COMMAND_MAX_CLAIMS_LINE_6.getComponent(new String[] { BukkitUtils.parseValue(accruedClaims), BukkitUtils.parseValue(Settings.ACCRUED_CLAIMS_MAX.getValue()) }));
        lines.add(spacer);
        lines.add(Lang.COMMAND_MAX_CLAIMS_LINE_7.getComponent(new String[] { BukkitUtils.parseValue(claimed), BukkitUtils.parseValue(maxClaims) }));
        lines.add(spacer);
        lines.add(Lang.COMMAND_MAX_CLAIMS_FOOTER.getComponent(null));

        for (Component line : lines) player.sendMessage(line);
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_NOT_A_CONSOLE_COMMAND.getComponent(null)));
    }
}
