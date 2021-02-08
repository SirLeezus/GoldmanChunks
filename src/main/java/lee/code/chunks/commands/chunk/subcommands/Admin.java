package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.SQLite;
import lee.code.chunks.lists.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Scanner;
import java.util.UUID;

public class Admin extends SubCommand {

    @Override
    public String getName() {
        return "admin";
    }

    @Override
    public String getDescription() {
        return "Administrator plugin operations.";
    }

    @Override
    public String getSyntax() {
        return "/chunk admin";
    }

    @Override
    public String getPermission() {
        return "mychunks.command.admin";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();

        if (args.length > 1) {

            String command = args[1];
            SQLite SQL = plugin.getSqLite();
            UUID uuid = player.getUniqueId();
            Chunk chunk = player.getLocation().getChunk();
            String chunkCord = plugin.getPU().formatChunk(chunk);

            switch (command) {

                case "unclaim":

                    if (SQL.isChunkClaimed(chunkCord)) {
                        UUID owner = SQL.getChunkOwnerUUID(chunkCord);
                        SQL.unClaimChunk(chunkCord, owner);
                        player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_ADMIN_UNCLAIM.getString(new String[]{chunkCord, Bukkit.getPlayer(owner).getName()}));
                    } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_ADMIN_UNCLAIM.getString(new String[]{chunkCord}));
                    break;

                case "unclaimall":

                    if (SQL.isChunkClaimed(chunkCord)) {
                        UUID owner = SQL.getChunkOwnerUUID(chunkCord);
                        SQL.deleteAllClaimedChunks(owner);
                        player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_ADMIN_UNCLAIMALL.getString(new String[]{Bukkit.getPlayer(owner).getName()}));
                    } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_ADMIN_UNCLAIM.getString(new String[]{chunkCord}));
                    break;

                case "bypass":

                    if (plugin.getData().hasAdminBypass(uuid)) {
                        plugin.getData().removeAdminBypass(uuid);
                        player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_ADMIN_BYPASS_DISABLED.getString(null));
                    } else {
                        plugin.getData().addAdminBypass(uuid);
                        player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_ADMIN_BYPASS_ENABLED.getString(null));
                    }
                    break;

                case "bonusclaims":

                    switch (args.length) {
                        case 2:
                            player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_ADMIN_BONUS_CLAIMS_ARG_2.getString(null));
                            break;
                        case 3:
                            player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_ADMIN_BONUS_CLAIMS_ARG_3.getString(null));
                            break;
                        case 4:
                            player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_ADMIN_BONUS_CLAIMS_ARG_4.getString(null));
                            break;

                        default:

                            int amount;
                            Scanner sellScanner = new Scanner(args[4]);
                            if (sellScanner.hasNextInt()) {
                                amount = Integer.parseInt(args[4]);
                            } else {
                                player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_ADMIN_BONUS_CLAIMS_AMOUNT.getString(new String[]{ args[4] }));
                                return;
                            }

                            Player target;
                            if (plugin.getPU().getOnlinePlayers().contains(args[3])) {
                                target = Bukkit.getPlayer(args[3]);
                            } else {
                                player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_PLAYER_NOT_ONLINE.getString(new String[]{ args[3] }));
                                return;
                            }

                            UUID targetUUID = target.getUniqueId();

                            switch (args[2]) {

                                case "add":
                                    SQL.addBonusClaims(targetUUID, amount);
                                    player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_ADMIN_BONUS_CLAIMS_ADD.getString(new String[] { plugin.getPU().formatAmount(amount), target.getName() }));
                                    break;
                                case "remove":
                                    SQL.removeBonusClaims(targetUUID, amount);
                                    player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_ADMIN_BONUS_CLAIMS_REMOVE.getString(new String[] { plugin.getPU().formatAmount(amount), target.getName() }));
                                    break;
                                case "set":
                                    SQL.setBonusClaims(targetUUID, amount);
                                    player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_ADMIN_BONUS_CLAIMS_SET.getString(new String[] { plugin.getPU().formatAmount(amount), target.getName() }));
                                    break;

                                default: player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_ADMIN_WRONG_ARG.getString(new String[] { args[2] }));
                            }
                    }
            }
        } else player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_ADMIN_ARGS.getString(null));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        console.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_NOT_A_CONSOLE_COMMAND.getString(null));
    }
}