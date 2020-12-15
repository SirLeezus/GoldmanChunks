package lee.code.mychunks.commands.subcommands;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.commands.SubCommand;
import lee.code.mychunks.database.SQLite;
import lee.code.mychunks.files.defaults.Lang;
import lee.code.mychunks.files.defaults.Settings;
import lee.code.mychunks.files.defaults.Values;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        return "mychunks.command.maxclaims";
    }

    @Override
    public void perform(Player player, String[] args) {
        MyChunks plugin = MyChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        UUID uuid = player.getUniqueId();

        int claims = SQL.getPlayerClaims(player);
        int claimed = SQL.getClaimedAmount(uuid);
        int bonusClaims = SQL.getBonusClaims(uuid);
        int accruedClaims = SQL.getAccruedClaims(uuid);
        int maxClaims = SQL.getMaxPlayerClaims(player);
        int time = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        String timePlayed = plugin.getUtility().formatTime(time);
        String timeRequired = plugin.getUtility().formatTime(Values.ACCRUED_CLAIMS_BASE_TIME_REQUIRED.getConfigValue());
        int givenAmount = Values.ACCRUED_CLAIMS_AMOUNT_GIVEN.getConfigValue();

        player.sendMessage(Lang.COMMAND_MAX_CLAIMS_HEADER.getConfigValue(null));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_MAX_CLAIMS_LINE_1.getConfigValue(new String[] { plugin.getUtility().formatAmount(claims) }));
        player.sendMessage(Lang.COMMAND_MAX_CLAIMS_LINE_2.getConfigValue(new String[] { plugin.getUtility().formatAmount(bonusClaims) }));
        player.sendMessage(Lang.COMMAND_MAX_CLAIMS_LINE_3.getConfigValue(new String[] { plugin.getUtility().formatAmount(claimed) }));
        player.sendMessage("");

        if (Settings.ACCRUED_CLAIMS_ENABLED.getConfigValue()) {
            player.sendMessage(Lang.COMMAND_MAX_CLAIMS_LINE_4.getConfigValue(new String[] { plugin.getUtility().format(timePlayed) }));
            player.sendMessage(Lang.COMMAND_MAX_CLAIMS_LINE_5.getConfigValue(new String[] { plugin.getUtility().formatAmount(givenAmount), plugin.getUtility().format(timeRequired) }));
            player.sendMessage(Lang.COMMAND_MAX_CLAIMS_LINE_6.getConfigValue(new String[] { plugin.getUtility().formatAmount(accruedClaims) }));
            player.sendMessage("");
        }
        player.sendMessage(Lang.COMMAND_MAX_CLAIMS_LINE_7.getConfigValue(new String[] { plugin.getUtility().formatAmount(claimed), plugin.getUtility().formatAmount(maxClaims) }));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_MAX_CLAIMS_FOOTER.getConfigValue(null));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getConfigValue(null));
    }
}
