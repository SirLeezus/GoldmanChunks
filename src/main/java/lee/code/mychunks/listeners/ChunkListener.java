package lee.code.mychunks.listeners;

import lee.code.mychunks.MyChunks;
import lee.code.mychunks.database.SQLite;
import lee.code.mychunks.files.defaults.Lang;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class ChunkListener implements Listener {

    private void warnMessage(Player player, boolean trusted, UUID owner, String type) {
        MyChunks plugin = MyChunks.getPlugin();
        String messageType;
        String messageTrusted;

        if (trusted) messageTrusted = plugin.getUtility().format("&atrue");
        else messageTrusted = plugin.getUtility().format("&cfalse");

        switch (type) {
            case "build":
                messageType = Lang.ITEM_SETTINGS_BUILD_NAME.getConfigValue(new String[] { plugin.getUtility().format("&cfalse") });
                break;
            case "break":
                messageType = Lang.ITEM_SETTINGS_BREAK_NAME.getConfigValue(new String[] { plugin.getUtility().format("&cfalse") });
                break;
            case "interact":
                messageType = Lang.ITEM_SETTINGS_INTERACT_NAME.getConfigValue(new String[] { plugin.getUtility().format("&cfalse") });
                break;
            case "pve":
                messageType = Lang.ITEM_SETTINGS_PVE_NAME.getConfigValue(new String[] { plugin.getUtility().format("&cfalse") });
                break;
            default:
                messageType = "ERROR";
        }

        TextComponent message = new TextComponent(Lang.ERROR_NO_CLAIM_PERMISSION.getConfigValue(new String[] { messageTrusted, messageType, Bukkit.getOfflinePlayer(owner).getName() }));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, message);
    }

    private void warnMessagePVP(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Lang.ERROR_PVP_DISABLED.getConfigValue(null)));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        MyChunks plugin = MyChunks.getPlugin();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        SQLite SQL = plugin.getSqLite();

        Chunk chunk = e.getBlock().getLocation().getChunk();
        String chunkCord = plugin.getUtility().formatChunk(chunk);

        if (!plugin.getData().hasAdminBypass(uuid)) {
            if (SQL.isChunkClaimed(chunkCord)) {
                if (!SQL.isChunkOwner(chunkCord, uuid)) {
                    UUID owner = SQL.getChunkOwnerUUID(chunkCord);
                    //chunk trusted check
                    if (SQL.isChunkTrusted(chunkCord, uuid)) {
                        if (!SQL.canTrustedBreak(chunkCord)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, "break");
                        }
                        //global trusted check
                    } else if (SQL.isGlobalTrusted(owner, uuid)) {
                        if (!SQL.canGlobalTrustedBreak(owner)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, "break");
                        }
                    } else {
                        e.setCancelled(true);
                        warnMessage(player, false, owner, "break");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHangingEntityBreak(HangingBreakByEntityEvent e) {
        MyChunks plugin = MyChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        Chunk chunk = e.getEntity().getLocation().getChunk();
        String chunkCord = plugin.getUtility().formatChunk(chunk);

        if (SQL.isChunkClaimed(chunkCord)) {

            if (e.getRemover() instanceof Player) {
                Player player = (Player) e.getRemover();
                UUID uuid = player.getUniqueId();

                if (!plugin.getData().hasAdminBypass(uuid)) {
                    if (!SQL.isChunkOwner(chunkCord, uuid)) {
                        UUID owner = SQL.getChunkOwnerUUID(chunkCord);
                        //chunk trusted check
                        if (SQL.isChunkTrusted(chunkCord, uuid)) {
                            if (!SQL.canTrustedBreak(chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, "break");
                            }
                            //global trusted check
                        } else if (SQL.isGlobalTrusted(owner, uuid)) {
                            if (!SQL.canGlobalTrustedBreak(owner)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, "break");
                            }
                        } else {
                            e.setCancelled(true);
                            warnMessage(player, false, owner, "break");
                        }
                    }
                }
            } else if (e.getCause().equals(HangingBreakEvent.RemoveCause.EXPLOSION)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onVehicleBreak(VehicleDestroyEvent e) {
        MyChunks plugin = MyChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        Chunk chunk = e.getVehicle().getLocation().getChunk();
        String chunkCord = plugin.getUtility().formatChunk(chunk);

        if (SQL.isChunkClaimed(chunkCord)) {
            if (e.getAttacker() instanceof Player) {
                Player player = (Player) e.getAttacker();
                UUID uuid = player.getUniqueId();
                UUID owner = SQL.getChunkOwnerUUID(chunkCord);

                if (!plugin.getData().hasAdminBypass(uuid)) {
                    if (!SQL.isChunkOwner(chunkCord, uuid)) {

                        if (SQL.isChunkTrusted(chunkCord, uuid)) {
                            if (!SQL.canTrustedBreak(chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, "break");
                            }
                        } else if (SQL.isGlobalTrusted(owner, uuid)) {
                            if (!SQL.canGlobalTrustedBreak(owner)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, "break");
                            }
                        } else {
                            e.setCancelled(true);
                            warnMessage(player, false, owner, "break");
                        }
                    }
                }
            } else if (!(e.getAttacker() instanceof Player)) {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        MyChunks plugin = MyChunks.getPlugin();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        SQLite SQL = plugin.getSqLite();

        Chunk chunk = e.getBlock().getLocation().getChunk();
        String chunkCord = plugin.getUtility().formatChunk(chunk);

        if (!plugin.getData().hasAdminBypass(uuid)) {
            if (SQL.isChunkClaimed(chunkCord)) {
                if (!SQL.isChunkOwner(chunkCord, uuid)) {
                    UUID owner = SQL.getChunkOwnerUUID(chunkCord);
                    //chunk trusted check
                    if (SQL.isChunkTrusted(chunkCord, uuid)) {
                        if (!SQL.canTrustedBuild(chunkCord)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, "build");
                        }
                        //global trusted check
                    } else if (SQL.isGlobalTrusted(owner, uuid)) {
                        if (!SQL.canGlobalTrustedBuild(owner)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, "build");
                        }
                    } else {
                        e.setCancelled(true);
                        warnMessage(player, false, owner, "build");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        MyChunks plugin = MyChunks.getPlugin();

        if (e.hasBlock()) {

            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            SQLite SQL = plugin.getSqLite();
            Chunk chunk = e.getClickedBlock().getLocation().getChunk();
            String chunkCord = plugin.getUtility().formatChunk(chunk);

            if (!plugin.getData().hasAdminBypass(uuid)) {
                if (SQL.isChunkClaimed(chunkCord)) {
                    if (!SQL.isChunkOwner(chunkCord, uuid)) {
                        UUID owner = SQL.getChunkOwnerUUID(chunkCord);
                        if (e.getClickedBlock().getType().isInteractable() || e.getAction() == Action.PHYSICAL) {

                            //chunk trusted check
                            if (SQL.isChunkTrusted(chunkCord, uuid)) {
                                if (!SQL.canTrustedInteract(chunkCord)) {
                                    e.setCancelled(true);
                                    warnMessage(player, true, owner, "interact");
                                }
                                //global trusted check
                            } else if (SQL.isGlobalTrusted(owner, uuid)) {
                                if (!SQL.canGlobalTrustedInteract(owner)) {
                                    e.setCancelled(true);
                                    warnMessage(player, true, owner, "interact");
                                }
                            } else {
                                e.setCancelled(true);
                                warnMessage(player, false, owner, "interact");
                            }

                        } else {

                            //chunk trusted check
                            if (SQL.isChunkTrusted(chunkCord, uuid)) {
                                if (!SQL.canTrustedBuild(chunkCord)) {
                                    e.setCancelled(true);
                                    warnMessage(player, true, owner, "build");
                                }
                                //global trusted check
                            } else if (SQL.isGlobalTrusted(owner, uuid)) {
                                if (!SQL.canGlobalTrustedBuild(owner)) {
                                    e.setCancelled(true);
                                    warnMessage(player, true, owner, "build");
                                }
                            } else {
                                e.setCancelled(true);
                                warnMessage(player, false, owner, "build");
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onArmorStandInteract(PlayerInteractAtEntityEvent e) {
        MyChunks plugin = MyChunks.getPlugin();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        SQLite SQL = plugin.getSqLite();

        if (e.getRightClicked() instanceof ArmorStand) {

            Chunk chunk = e.getRightClicked().getLocation().getChunk();
            String chunkCord = plugin.getUtility().formatChunk(chunk);

            if (!plugin.getData().hasAdminBypass(uuid)) {
                if (SQL.isChunkClaimed(chunkCord)) {
                    if (!SQL.isChunkOwner(chunkCord, uuid)) {
                        UUID owner = SQL.getChunkOwnerUUID(chunkCord);
                        //chunk trusted check
                        if (SQL.isChunkTrusted(chunkCord, uuid)) {
                            if (!SQL.canTrustedInteract(chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, "interact");
                            }
                            //global trusted check
                        } else if (SQL.isGlobalTrusted(owner, uuid)) {
                            if (!SQL.canGlobalTrustedInteract(owner)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, "interact");
                            }
                        } else {
                            e.setCancelled(true);
                            warnMessage(player, false, owner, "interact");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        MyChunks plugin = MyChunks.getPlugin();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        SQLite SQL = plugin.getSqLite();

        Chunk chunk = e.getRightClicked().getLocation().getChunk();
        String chunkCord = plugin.getUtility().formatChunk(chunk);

        if (!(e.getRightClicked() instanceof Player)) {

            if (!plugin.getData().hasAdminBypass(uuid)) {
                if (SQL.isChunkClaimed(chunkCord)) {
                    if (!SQL.isChunkOwner(chunkCord, uuid)) {
                        UUID owner = SQL.getChunkOwnerUUID(chunkCord);
                        //chunk trusted check
                        if (SQL.isChunkTrusted(chunkCord, uuid)) {
                            if (!SQL.canTrustedInteract(chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, "interact");
                            }
                            //global trusted check
                        } else if (SQL.isGlobalTrusted(owner, uuid)) {
                            if (!SQL.canGlobalTrustedInteract(owner)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, "interact");
                            }
                        } else {
                            e.setCancelled(true);
                            warnMessage(player, false, owner, "interact");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        MyChunks plugin = MyChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();

        Chunk chunk = e.getEntity().getLocation().getChunk();
        String chunkCord = plugin.getUtility().formatChunk(chunk);

        if (SQL.isChunkClaimed(chunkCord)) {

            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) || e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                e.setCancelled(true);
                return;
            }

            //pvp
            if (e.getDamager() instanceof Player) {
                Player player = (Player) e.getDamager();
                UUID uuid = player.getUniqueId();

                if (!plugin.getData().hasAdminBypass(uuid)) {

                    if (e.getEntity() instanceof Player) {
                        if (!SQL.canChunkPVP(chunkCord)) {
                            e.setCancelled(true);
                            warnMessagePVP(player);
                        }
                        //pve
                    } else if (!(e.getEntity() instanceof Monster)) {
                        UUID owner = SQL.getChunkOwnerUUID(chunkCord);
                        if (!SQL.isChunkOwner(chunkCord, uuid)) {
                            if (SQL.isGlobalTrusted(owner, uuid)) {
                                if (!SQL.canGlobalTrustedPVE(owner)) {
                                    e.setCancelled(true);
                                    warnMessage(player, true, owner, "pve");
                                }
                            } else if (SQL.isChunkTrusted(chunkCord, uuid)) {
                                if (!SQL.canTrustedPVE(chunkCord)) {
                                    e.setCancelled(true);
                                    warnMessage(player, true, owner, "pve");
                                }
                            } else {
                                e.setCancelled(true);
                                warnMessage(player, false, owner, "pve");
                            }
                        }
                    }
                }
            } else if (e.getDamager() instanceof Projectile) {

                Projectile projectile = (Projectile) e.getDamager();

                //projectile pvp & pve
                if (projectile.getShooter() instanceof Player) {
                    Player player = (Player) projectile.getShooter();
                    UUID uuid = player.getUniqueId();

                    if (!plugin.getData().hasAdminBypass(uuid)) {

                        //pvp
                        if (e.getEntity() instanceof Player) {
                            if (!SQL.canChunkPVP(chunkCord)) {
                                e.setCancelled(true);
                                warnMessagePVP(player);
                            }
                            //pve
                        } else if (!(e.getEntity() instanceof Monster)) {
                            UUID owner = SQL.getChunkOwnerUUID(chunkCord);
                            if (!SQL.isChunkOwner(chunkCord, uuid)) {
                                if (SQL.isGlobalTrusted(owner, uuid)) {
                                    if (!SQL.canGlobalTrustedPVE(owner)) {
                                        e.setCancelled(true);
                                        warnMessage(player, true, owner, "pve");
                                    }
                                } else if (SQL.isChunkTrusted(chunkCord, uuid)) {
                                    if (!SQL.canTrustedPVE(chunkCord)) {
                                        e.setCancelled(true);
                                        warnMessage(player, true, owner, "pve");
                                    }
                                } else {
                                    e.setCancelled(true);
                                    warnMessage(player, false, owner, "pve");
                                }
                            }
                        }
                    }
                } else if (projectile.getShooter() instanceof Monster) e.setCancelled(true);
            }
        }
    }

    //explode handler
    @EventHandler
    public void onExplodeEvent(EntityExplodeEvent e) {
        MyChunks plugin = MyChunks.getPlugin();

        for (Block block : new ArrayList<Block>(e.blockList())) {
            Chunk chunk = block.getLocation().getChunk();
            String chunkCord = plugin.getUtility().formatChunk(chunk);
            if (plugin.getSqLite().isChunkClaimed(chunkCord)) {
                if (!plugin.getSqLite().canChunkExplode(chunkCord)) e.blockList().remove(block);
            }
        }
    }

    //piston move handler
    @EventHandler
    public void onPistonMove(BlockPistonExtendEvent e) {
        MyChunks plugin = MyChunks.getPlugin();

        //check piston location
        Chunk chunk = e.getBlock().getLocation().getChunk();
        String chunkCord = plugin.getUtility().formatChunk(chunk);
        if (!plugin.getSqLite().isChunkClaimed(chunkCord)) {
            e.setCancelled(true);
            return;
        }

        //check piston moved blocks
        for (Block block : new ArrayList<Block>(e.getBlocks())) {
            Chunk movedBlockChunk = block.getLocation().getChunk();
            String movedBlockChunkCords = plugin.getUtility().formatChunk(movedBlockChunk);
            if (!plugin.getSqLite().isChunkClaimed(movedBlockChunkCords)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    //monster spawn handler
    @EventHandler
    public void onMonsterSpawn(CreatureSpawnEvent e) {
        MyChunks plugin = MyChunks.getPlugin();

        if (e.getEntity() instanceof Monster) {

            Chunk chunk = e.getEntity().getLocation().getChunk();
            String chunkCord = plugin.getUtility().formatChunk(chunk);

            //check if chunk is claimed or they own it
            if (plugin.getSqLite().isChunkClaimed(chunkCord)) {
                if (!plugin.getSqLite().canChunkSpawnMonsters(chunkCord)) e.setCancelled(true);
            }
        }
    }

    //auto claim handler
    @EventHandler
    public void onChunkChange(PlayerMoveEvent e) {
        MyChunks plugin = MyChunks.getPlugin();
        if (plugin.getData().isPlayerAutoClaiming(e.getPlayer().getUniqueId())) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            SQLite SQL = plugin.getSqLite();

            Chunk chunk = player.getLocation().getChunk();
            String chunkCord = plugin.getUtility().formatChunk(chunk);

            if (!plugin.getSqLite().isChunkClaimed(chunkCord)) {

                Collection<Chunk> chunksAroundPlayer = plugin.getUtility().getChunksAroundPlayer(player);
                Chunk lastChunkClaim = plugin.getData().getPlayerLastAutoClaim(uuid);

                if (!chunksAroundPlayer.contains(lastChunkClaim)) {
                    plugin.getData().removePlayerAutoClaim(uuid);
                    player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_COMMAND_AUTO_CLAIM.getConfigValue(null));
                    return;
                }

                int playerClaimAmount = SQL.getClaimedAmount(uuid);
                int playerMaxClaims = SQL.getMaxPlayerClaims(player);

                if (playerClaimAmount < playerMaxClaims) {
                    playerClaimAmount++;
                    SQL.claimChunk(chunkCord, uuid);
                    plugin.getData().setPlayerAutoClaim(uuid, chunk);
                    player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.COMMAND_CLAIM_SUCCESSFUL.getConfigValue(new String[]{chunkCord, plugin.getUtility().formatAmount(playerClaimAmount), plugin.getUtility().formatAmount(playerMaxClaims)}));
                    plugin.getUtility().renderChunkBorder(player, chunk, "claim");
                } else {
                    plugin.getData().removePlayerAutoClaim(uuid);
                    player.sendMessage(Lang.PREFIX.getConfigValue(null) + Lang.ERROR_COMMAND_CLAIM_MAXED.getConfigValue(new String[] { plugin.getUtility().formatAmount(playerClaimAmount), plugin.getUtility().formatAmount(playerMaxClaims) }));
                }

            } else if (!plugin.getData().getPlayerLastAutoClaim(uuid).equals(chunk)) plugin.getData().setPlayerAutoClaim(player.getUniqueId(), chunk);
        }
    }
}
