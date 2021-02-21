package lee.code.chunks.listeners;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.SQLite;
import lee.code.chunks.lists.Lang;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

public class ChunkListener implements Listener {

    private void warnMessage(Player player, boolean trusted, UUID owner, String type) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        String messageType;
        String messageTrusted;

        if (trusted) messageTrusted = Lang.TRUE.getString(null);
        else messageTrusted = Lang.FALSE.getString(null);

        switch (type) {
            case "build":
                messageType = Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[] { Lang.FALSE.getString(null) });
                break;
            case "break":
                messageType = Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[] { Lang.FALSE.getString(null) });
                break;
            case "interact":
                messageType = Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[] { Lang.FALSE.getString(null) });
                break;
            case "pve":
                messageType = Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[] { Lang.FALSE.getString(null) });
                break;
            default:
                messageType = "ERROR";
        }

        player.sendActionBar(Lang.ERROR_NO_CLAIM_PERMISSION.getString(new String[] { messageTrusted, messageType, Bukkit.getOfflinePlayer(owner).getName() }));
    }

    private void warnMessagePVP(Player player) {
        player.sendActionBar(Lang.ERROR_PVP_DISABLED.getString(null));
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        SQLite SQL = plugin.getSqLite();

        Chunk chunk = e.getBlock().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

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
            } else if (SQL.isAdminChunk(chunkCord)) {
                if (!SQL.canAdminChunkBreak(chunkCord)) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHangingEntityBreak(HangingBreakByEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        Chunk chunk = e.getEntity().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

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
        } else if (SQL.isAdminChunk(chunkCord)) {
            if (e.getRemover() instanceof Player) {
                Player player = (Player) e.getRemover();
                UUID uuid = player.getUniqueId();
                if (!plugin.getData().hasAdminBypass(uuid)) {
                    if (!SQL.canAdminChunkBreak(chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onVehicleBreak(VehicleDestroyEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();
        Chunk chunk = e.getVehicle().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

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
        } else if (SQL.isAdminChunk(chunkCord)) {
            if (e.getAttacker() instanceof Player) {
                Player player = (Player) e.getAttacker();
                UUID uuid = player.getUniqueId();
                if (!plugin.getData().hasAdminBypass(uuid)) {
                    if (!SQL.canAdminChunkBreak(chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        SQLite SQL = plugin.getSqLite();

        Chunk chunk = e.getBlock().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

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
            } else if (SQL.isAdminChunk(chunkCord)) {
                if (!SQL.canAdminChunkBuild(chunkCord)) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();

        if (e.hasBlock()) {

            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            SQLite SQL = plugin.getSqLite();
            Chunk chunk = e.getClickedBlock().getLocation().getChunk();
            String chunkCord = plugin.getPU().formatChunkLocation(chunk);

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
                } else if (SQL.isAdminChunk(chunkCord)) {
                    if (e.getClickedBlock().getType().isInteractable() || e.getAction() == Action.PHYSICAL) {
                        if (!SQL.canAdminChunkInteract(chunkCord)) e.setCancelled(true);
                    } else {
                        if (!SQL.canAdminChunkBuild(chunkCord)) e.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onArmorStandInteract(PlayerInteractAtEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        SQLite SQL = plugin.getSqLite();

        if (e.getRightClicked() instanceof ArmorStand) {

            Chunk chunk = e.getRightClicked().getLocation().getChunk();
            String chunkCord = plugin.getPU().formatChunkLocation(chunk);

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
                } else if (SQL.isAdminChunk(chunkCord)) {
                    if (!SQL.canAdminChunkInteract(chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        SQLite SQL = plugin.getSqLite();

        Chunk chunk = e.getRightClicked().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

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
                } else if (SQL.isAdminChunk(chunkCord)) {
                    if (!SQL.canAdminChunkInteract(chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();

        Chunk chunk = e.getEntity().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

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
            //admin chunk claim
        } else if (SQL.isAdminChunk(chunkCord)) {

            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) || e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                e.setCancelled(true);
                return;
            }

            if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
                UUID uuid = e.getDamager().getUniqueId();
                if (!plugin.getData().hasAdminBypass(uuid)) {
                    if (!SQL.canAdminChunkPVP(chunkCord)) e.setCancelled(true);
                }
            } else if (e.getDamager() instanceof Player) {
                UUID uuid = e.getDamager().getUniqueId();
                if (!plugin.getData().hasAdminBypass(uuid)) {
                    if (!SQL.canAdminChunkPVE(chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    //explode handler
    @EventHandler
    public void onExplodeEvent(EntityExplodeEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();

        for (Block block : new ArrayList<>(e.blockList())) {
            Chunk chunk = block.getLocation().getChunk();
            String chunkCord = plugin.getPU().formatChunkLocation(chunk);
            if (SQL.isChunkClaimed(chunkCord)) {
                if (!SQL.canChunkExplode(chunkCord)) e.blockList().remove(block);
            } else if (SQL.isAdminChunk(chunkCord)) {
                if (!SQL.canAdminChunkExplode(chunkCord)) e.blockList().remove(block);
            }
        }
    }

    //piston move handler
    @EventHandler
    public void onPistonMove(BlockPistonExtendEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        SQLite SQL = plugin.getSqLite();

        //check piston location
        Chunk chunk = e.getBlock().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);
        if (!SQL.isChunkClaimed(chunkCord)) {
            e.setCancelled(true);
            return;
        }

        if (SQL.isAdminChunk(chunkCord)) return;

        //check piston moved blocks
        for (Block block : new ArrayList<>(e.getBlocks())) {
            Chunk movedBlockChunk = block.getLocation().getChunk();
            String movedBlockChunkCords = plugin.getPU().formatChunkLocation(movedBlockChunk);
            if (!plugin.getSqLite().isChunkClaimed(movedBlockChunkCords)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    //monster spawn handler
    @EventHandler
    public void onMonsterSpawn(CreatureSpawnEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();

        if (e.getEntity() instanceof Monster) {

            Chunk chunk = e.getEntity().getLocation().getChunk();
            String chunkCord = plugin.getPU().formatChunkLocation(chunk);
            SQLite SQL = plugin.getSqLite();

            if (SQL.isChunkClaimed(chunkCord)) {
                if (!SQL.canChunkSpawnMonsters(chunkCord)) e.setCancelled(true);
            } else if (SQL.isAdminChunk(chunkCord)) {
                if (!SQL.canAdminChunkSpawnMonsters(chunkCord)) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();

        Chunk chunk = e.getEntity().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);
        SQLite SQL = plugin.getSqLite();

        if (SQL.isChunkClaimed(chunkCord) || SQL.isAdminChunk(chunkCord)) {
            e.getDrops().clear();
            e.setKeepInventory(true);
            e.setKeepLevel(true);
        }
    }

    //auto claim handler
    @EventHandler
    public void onChunkChange(PlayerMoveEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        if (plugin.getData().isPlayerAutoClaiming(e.getPlayer().getUniqueId())) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            SQLite SQL = plugin.getSqLite();

            Chunk chunk = player.getLocation().getChunk();
            String chunkCord = plugin.getPU().formatChunkLocation(chunk);

            if (!SQL.isChunkClaimed(chunkCord)) {

                if (SQL.isAdminChunk(chunkCord)) {
                    plugin.getData().removePlayerAutoClaim(uuid);
                    player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_ADMIN_CLAIMED.getString(null));
                    return;
                }

                Collection<Chunk> chunksAroundPlayer = plugin.getPU().getChunksAroundPlayer(player);
                Chunk lastChunkClaim = plugin.getData().getPlayerLastAutoClaim(uuid);

                if (!chunksAroundPlayer.contains(lastChunkClaim)) {
                    plugin.getData().removePlayerAutoClaim(uuid);
                    player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_AUTO_CLAIM.getString(null));
                    return;
                }

                int playerClaimAmount = SQL.getClaimedAmount(uuid);
                int playerMaxClaims = SQL.getMaxPlayerClaims(player);

                if (playerClaimAmount < playerMaxClaims) {
                    playerClaimAmount++;
                    SQL.claimChunk(chunkCord, uuid);
                    plugin.getData().setPlayerAutoClaim(uuid, chunk);
                    player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_CLAIM_SUCCESSFUL.getString(new String[]{chunkCord, plugin.getPU().formatAmount(playerClaimAmount), plugin.getPU().formatAmount(playerMaxClaims)}));
                    plugin.getPU().renderChunkBorder(player, chunk, "claim");
                } else {
                    plugin.getData().removePlayerAutoClaim(uuid);
                    player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_CLAIM_MAXED.getString(new String[] { plugin.getPU().formatAmount(playerClaimAmount), plugin.getPU().formatAmount(playerMaxClaims) }));
                }

            } else if (!plugin.getData().getPlayerLastAutoClaim(uuid).equals(chunk)) plugin.getData().setPlayerAutoClaim(player.getUniqueId(), chunk);
        }
    }
}
