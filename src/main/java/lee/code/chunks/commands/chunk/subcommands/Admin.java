package lee.code.chunks.commands.chunk.subcommands;

import lee.code.chunks.Data;
import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.commands.SubCommand;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.Lang;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        return "chunk.command.admin";
    }

    @Override
    public void perform(Player player, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        Data data = plugin.getData();
        PU pu = plugin.getPU();

        if (args.length > 1) {

            String command = args[1];
            UUID uuid = player.getUniqueId();
            Chunk chunk = player.getLocation().getChunk();
            String chunkCord = pu.formatChunkLocation(chunk);

            switch (command) {
                case "unclaim":
                    if (cache.isChunkClaimed(chunkCord)) {
                        UUID owner = cache.getChunkOwnerUUID(chunkCord);
                        String ownerName = cache.getChunkOwnerName(chunkCord);
                        cache.unclaimChunk(chunkCord, owner);
                        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_UNCLAIM.getComponent(new String[]{ chunkCord, ownerName })));
                    } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ADMIN_UNCLAIM.getComponent(new String[]{ chunkCord })));
                    break;

                case "unclaimall":
                    if (cache.isChunkClaimed(chunkCord)) {
                        UUID owner = cache.getChunkOwnerUUID(chunkCord);
                        String ownerName = cache.getChunkOwnerName(chunkCord);
                        cache.unclaimAllChunks(owner);
                        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_UNCLAIMALL.getComponent(new String[]{ ownerName })));
                    } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ADMIN_UNCLAIM.getComponent(new String[]{ chunkCord })));
                    break;

                case "bypass":
                    if (data.hasAdminBypass(uuid)) {
                        data.removeAdminBypass(uuid);
                        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_BYPASS_DISABLED.getComponent(null)));
                    } else {
                        data.addAdminBypass(uuid);
                        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_BYPASS_ENABLED.getComponent(null)));
                    }
                    break;

                case "bonusclaims":
                    switch (args.length) {
                        case 2 -> player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ADMIN_BONUS_CLAIMS_ARG_2.getComponent(null)));
                        case 3 -> player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ADMIN_BONUS_CLAIMS_ARG_3.getComponent(null)));
                        case 4 -> player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ADMIN_BONUS_CLAIMS_ARG_4.getComponent(null)));

                        default -> {
                            if (pu.containOnlyNumbers(args[4])) {
                                int amount = Integer.parseInt(args[4]);
                                OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[3]);
                                if (target != null) {
                                    UUID targetUUID = target.getUniqueId();

                                    switch (args[2]) {
                                        case "add" -> {
                                            cache.addBonusClaimsAmount(targetUUID, amount);
                                            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_BONUS_CLAIMS_ADD.getComponent(new String[]{ pu.formatAmount(amount), target.getName() })));
                                        }
                                        case "remove" -> {
                                            cache.removeBonusClaimsAmount(targetUUID, amount);
                                            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_BONUS_CLAIMS_REMOVE.getComponent(new String[]{ pu.formatAmount(amount), target.getName() })));
                                        }
                                        case "set" -> {
                                            cache.setBonusClaimsAmount(targetUUID, amount);
                                            player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_BONUS_CLAIMS_SET.getComponent(new String[]{ pu.formatAmount(amount), target.getName() })));
                                        }
                                        default -> player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ADMIN_WRONG_ARG.getComponent(new String[]{args[2]})));
                                    }
                                } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[]{args[3]})));
                            } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ADMIN_BONUS_CLAIMS_AMOUNT.getComponent(new String[]{args[4]})));
                        }
                    }
            }
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ADMIN_ARGS.getComponent(null)));
    }

    @Override
    public void performConsole(CommandSender console, String[] args) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        PU pu = plugin.getPU();

        if (args.length > 1) {
            String command = args[1];

            if (command.equals("bonusclaims")) {
                switch (args.length) {
                    case 2 -> console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ADMIN_BONUS_CLAIMS_ARG_2.getComponent(null)));
                    case 3 -> console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ADMIN_BONUS_CLAIMS_ARG_3.getComponent(null)));
                    case 4 -> console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ADMIN_BONUS_CLAIMS_ARG_4.getComponent(null)));

                    default -> {
                        if (pu.containOnlyNumbers(args[4])) {
                            int amount = Integer.parseInt(args[4]);
                            OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[3]);
                            if (target != null) {
                                UUID targetUUID = target.getUniqueId();

                                switch (args[2]) {
                                    case "add" -> {
                                        cache.addBonusClaimsAmount(targetUUID, amount);
                                        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_BONUS_CLAIMS_ADD.getComponent(new String[]{ pu.formatAmount(amount), target.getName() })));
                                    }
                                    case "remove" -> {
                                        cache.removeBonusClaimsAmount(targetUUID, amount);
                                        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_BONUS_CLAIMS_REMOVE.getComponent(new String[]{ pu.formatAmount(amount), target.getName() })));
                                    }
                                    case "set" -> {
                                        cache.setBonusClaimsAmount(targetUUID, amount);
                                        console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.COMMAND_ADMIN_BONUS_CLAIMS_SET.getComponent(new String[]{ pu.formatAmount(amount), target.getName() })));
                                    }
                                    default -> console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ADMIN_WRONG_ARG.getComponent(new String[]{ args[2] })));
                                }
                            } else console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_PLAYER_NOT_FOUND.getComponent(new String[]{ args[3] })));
                        } else console.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_ADMIN_BONUS_CLAIMS_AMOUNT.getComponent(new String[]{ args[4] })));
                    }
                }
            }
        }
    }
}