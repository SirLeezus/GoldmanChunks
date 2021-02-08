package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.SQLite;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.Values;
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
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        UUID uuid = player.getUniqueId();

        int claims = SQL.getPlayerClaims(player);
        int claimed = SQL.getClaimedAmount(uuid);
        int bonusClaims = SQL.getBonusClaims(uuid);
        int accruedClaims = SQL.getAccruedClaims(uuid);
        int maxClaims = SQL.getMaxPlayerClaims(player);
        int time = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
        String timePlayed = plugin.getpU().formatTime(time);
        String timeRequired = plugin.getpU().formatTime(Values.ACCRUED_CLAIMS_BASE_TIME_REQUIRED.getValue());
        int givenAmount = Values.ACCRUED_CLAIMS_AMOUNT_GIVEN.getValue();

        player.sendMessage(Lang.COMMAND_MAX_CLAIMS_HEADER.getString(null));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_MAX_CLAIMS_LINE_1.getString(new String[] { plugin.getpU().formatAmount(claims) }));
        player.sendMessage(Lang.COMMAND_MAX_CLAIMS_LINE_2.getString(new String[] { plugin.getpU().formatAmount(bonusClaims) }));
        player.sendMessage(Lang.COMMAND_MAX_CLAIMS_LINE_3.getString(new String[] { plugin.getpU().formatAmount(claimed) }));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_MAX_CLAIMS_LINE_4.getString(new String[] { plugin.getpU().format(timePlayed) }));
        player.sendMessage(Lang.COMMAND_MAX_CLAIMS_LINE_5.getString(new String[] { plugin.getpU().formatAmount(givenAmount), plugin.getpU().format(timeRequired) }));
        player.sendMessage(Lang.COMMAND_MAX_CLAIMS_LINE_6.getString(new String[] { plugin.getpU().formatAmount(accruedClaims) }));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_MAX_CLAIMS_LINE_7.getString(new String[] { plugin.getpU().formatAmount(claimed), plugin.getpU().formatAmount(maxClaims) }));
        player.sendMessage("");
        player.sendMessage(Lang.COMMAND_MAX_CLAIMS_FOOTER.getString(null));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}
