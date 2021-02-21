package lee.code.chunks.listeners;

import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.database.Cache;
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
        Cache cache = plugin.getCache();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        Chunk chunk = e.getBlock().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (!plugin.getData().hasAdminBypass(uuid)) {
            if (cache.isChunkClaimed(chunkCord)) {
                if (!cache.isChunkOwner(chunkCord, uuid)) {
                    UUID owner = cache.getChunkOwnerUUID(chunkCord);
                    //chunk trusted check
                    if (cache.isChunkTrusted(chunkCord, uuid)) {
                        if (!cache.canChunkTrustedBreak(chunkCord)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, "break");
                        }
                        //global trusted check
                    } else if (cache.isGlobalTrusted(owner, uuid)) {
                        if (!cache.canGlobalTrustedBreak(owner)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, "break");
                        }
                    } else {
                        e.setCancelled(true);
                        warnMessage(player, false, owner, "break");
                    }
                }
            } else if (cache.isAdminChunk(chunkCord)) {
                if (!cache.canAdminChunkBreak(chunkCord)) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHangingEntityBreak(HangingBreakByEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        Chunk chunk = e.getEntity().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (cache.isChunkClaimed(chunkCord)) {

            if (e.getRemover() instanceof Player) {
                Player player = (Player) e.getRemover();
                UUID uuid = player.getUniqueId();

                if (!plugin.getData().hasAdminBypass(uuid)) {
                    if (!cache.isChunkOwner(chunkCord, uuid)) {
                        UUID owner = cache.getChunkOwnerUUID(chunkCord);
                        //chunk trusted check
                        if (cache.isChunkTrusted(chunkCord, uuid)) {
                            if (!cache.canChunkTrustedBreak(chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, "break");
                            }
                            //global trusted check
                        } else if (cache.isGlobalTrusted(owner, uuid)) {
                            if (!cache.canGlobalTrustedBreak(owner)) {
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
        } else if (cache.isAdminChunk(chunkCord)) {
            if (e.getRemover() instanceof Player) {
                Player player = (Player) e.getRemover();
                UUID uuid = player.getUniqueId();
                if (!plugin.getData().hasAdminBypass(uuid)) {
                    if (!cache.canAdminChunkBreak(chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onVehicleBreak(VehicleDestroyEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        Chunk chunk = e.getVehicle().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (cache.isChunkClaimed(chunkCord)) {
            if (e.getAttacker() instanceof Player) {
                Player player = (Player) e.getAttacker();
                UUID uuid = player.getUniqueId();
                UUID owner = cache.getChunkOwnerUUID(chunkCord);

                if (!plugin.getData().hasAdminBypass(uuid)) {
                    if (!cache.isChunkOwner(chunkCord, uuid)) {

                        if (cache.isChunkTrusted(chunkCord, uuid)) {
                            if (!cache.canChunkTrustedBreak(chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, "break");
                            }
                        } else if (cache.isGlobalTrusted(owner, uuid)) {
                            if (!cache.canGlobalTrustedBreak(owner)) {
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
        } else if (cache.isAdminChunk(chunkCord)) {
            if (e.getAttacker() instanceof Player) {
                Player player = (Player) e.getAttacker();
                UUID uuid = player.getUniqueId();
                if (!plugin.getData().hasAdminBypass(uuid)) {
                    if (!cache.canAdminChunkBreak(chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Chunk chunk = e.getBlock().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (!plugin.getData().hasAdminBypass(uuid)) {
            if (cache.isChunkClaimed(chunkCord)) {
                if (!cache.isChunkOwner(chunkCord, uuid)) {
                    UUID owner = cache.getChunkOwnerUUID(chunkCord);
                    //chunk trusted check
                    if (cache.isChunkTrusted(chunkCord, uuid)) {
                        if (!cache.canChunkTrustedBuild(chunkCord)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, "build");
                        }
                        //global trusted check
                    } else if (cache.isGlobalTrusted(owner, uuid)) {
                        if (!cache.canGlobalTrustedBuild(owner)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, "build");
                        }
                    } else {
                        e.setCancelled(true);
                        warnMessage(player, false, owner, "build");
                    }
                }
            } else if (cache.isAdminChunk(chunkCord)) {
                if (!cache.canAdminChunkBuild(chunkCord)) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        if (e.hasBlock()) {

            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();

            if (e.getClickedBlock() != null) {
                Chunk chunk = e.getClickedBlock().getLocation().getChunk();
                String chunkCord = plugin.getPU().formatChunkLocation(chunk);

                if (!plugin.getData().hasAdminBypass(uuid)) {
                    if (cache.isChunkClaimed(chunkCord)) {
                        if (!cache.isChunkOwner(chunkCord, uuid)) {
                            UUID owner = cache.getChunkOwnerUUID(chunkCord);
                            if (e.getClickedBlock().getType().isInteractable() || e.getAction() == Action.PHYSICAL) {

                                //chunk trusted check
                                if (cache.isChunkTrusted(chunkCord, uuid)) {
                                    if (!cache.canChunkTrustedInteract(chunkCord)) {
                                        e.setCancelled(true);
                                        warnMessage(player, true, owner, "interact");
                                    }
                                    //global trusted check
                                } else if (cache.isGlobalTrusted(owner, uuid)) {
                                    if (!cache.canGlobalTrustedInteract(owner)) {
                                        e.setCancelled(true);
                                        warnMessage(player, true, owner, "interact");
                                    }
                                } else {
                                    e.setCancelled(true);
                                    warnMessage(player, false, owner, "interact");
                                }

                            } else {

                                //chunk trusted check
                                if (cache.isChunkTrusted(chunkCord, uuid)) {
                                    if (!cache.canChunkTrustedBuild(chunkCord)) {
                                        e.setCancelled(true);
                                        warnMessage(player, true, owner, "build");
                                    }
                                    //global trusted check
                                } else if (cache.isGlobalTrusted(owner, uuid)) {
                                    if (!cache.canGlobalTrustedBuild(owner)) {
                                        e.setCancelled(true);
                                        warnMessage(player, true, owner, "build");
                                    }
                                } else {
                                    e.setCancelled(true);
                                    warnMessage(player, false, owner, "build");
                                }
                            }
                        }
                    } else if (cache.isAdminChunk(chunkCord)) {
                        if (e.getClickedBlock().getType().isInteractable() || e.getAction() == Action.PHYSICAL) {
                            if (!cache.canAdminChunkInteract(chunkCord)) e.setCancelled(true);
                        } else {
                            if (!cache.canAdminChunkBuild(chunkCord)) e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onArmorStandInteract(PlayerInteractAtEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (e.getRightClicked() instanceof ArmorStand) {

            Chunk chunk = e.getRightClicked().getLocation().getChunk();
            String chunkCord = plugin.getPU().formatChunkLocation(chunk);

            if (!plugin.getData().hasAdminBypass(uuid)) {
                if (cache.isChunkClaimed(chunkCord)) {
                    if (!cache.isChunkOwner(chunkCord, uuid)) {
                        UUID owner = cache.getChunkOwnerUUID(chunkCord);
                        //chunk trusted check
                        if (cache.isChunkTrusted(chunkCord, uuid)) {
                            if (!cache.canChunkTrustedInteract(chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, "interact");
                            }
                            //global trusted check
                        } else if (cache.isGlobalTrusted(owner, uuid)) {
                            if (!cache.canGlobalTrustedInteract(owner)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, "interact");
                            }
                        } else {
                            e.setCancelled(true);
                            warnMessage(player, false, owner, "interact");
                        }
                    }
                } else if (cache.isAdminChunk(chunkCord)) {
                    if (!cache.canAdminChunkInteract(chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        Chunk chunk = e.getRightClicked().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (!(e.getRightClicked() instanceof Player)) {

            if (!plugin.getData().hasAdminBypass(uuid)) {
                if (cache.isChunkClaimed(chunkCord)) {
                    if (!cache.isChunkOwner(chunkCord, uuid)) {
                        UUID owner = cache.getChunkOwnerUUID(chunkCord);
                        //chunk trusted check
                        if (cache.isChunkTrusted(chunkCord, uuid)) {
                            if (!cache.canChunkTrustedInteract(chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, "interact");
                            }
                            //global trusted check
                        } else if (cache.isGlobalTrusted(owner, uuid)) {
                            if (!cache.canGlobalTrustedInteract(owner)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, "interact");
                            }
                        } else {
                            e.setCancelled(true);
                            warnMessage(player, false, owner, "interact");
                        }
                    }
                } else if (cache.isAdminChunk(chunkCord)) {
                    if (!cache.canAdminChunkInteract(chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        Chunk chunk = e.getEntity().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (cache.isChunkClaimed(chunkCord)) {

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
                        if (!cache.canChunkPvP(chunkCord)) {
                            e.setCancelled(true);
                            warnMessagePVP(player);
                        }
                        //pve
                    } else if (!(e.getEntity() instanceof Monster)) {
                        UUID owner = cache.getChunkOwnerUUID(chunkCord);
                        if (!cache.isChunkOwner(chunkCord, uuid)) {
                            if (cache.isGlobalTrusted(owner, uuid)) {
                                if (!cache.canGlobalTrustedPvE(owner)) {
                                    e.setCancelled(true);
                                    warnMessage(player, true, owner, "pve");
                                }
                            } else if (cache.isChunkTrusted(chunkCord, uuid)) {
                                if (!cache.canChunkTrustedPvE(chunkCord)) {
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
                            if (!cache.canChunkPvP(chunkCord)) {
                                e.setCancelled(true);
                                warnMessagePVP(player);
                            }
                            //pve
                        } else if (!(e.getEntity() instanceof Monster)) {
                            UUID owner = cache.getChunkOwnerUUID(chunkCord);
                            if (!cache.isChunkOwner(chunkCord, uuid)) {
                                if (cache.isGlobalTrusted(owner, uuid)) {
                                    if (!cache.canGlobalTrustedPvE(owner)) {
                                        e.setCancelled(true);
                                        warnMessage(player, true, owner, "pve");
                                    }
                                } else if (cache.isChunkTrusted(chunkCord, uuid)) {
                                    if (!cache.canChunkTrustedPvE(chunkCord)) {
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
        } else if (cache.isAdminChunk(chunkCord)) {

            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) || e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                e.setCancelled(true);
                return;
            }

            if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
                UUID uuid = e.getDamager().getUniqueId();
                if (!plugin.getData().hasAdminBypass(uuid)) {
                    if (!cache.canAdminChunkPvP(chunkCord)) e.setCancelled(true);
                }
            } else if (e.getDamager() instanceof Player) {
                UUID uuid = e.getDamager().getUniqueId();
                if (!plugin.getData().hasAdminBypass(uuid)) {
                    if (!cache.canAdminChunkPvE(chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    //explode handler
    @EventHandler
    public void onExplodeEvent(EntityExplodeEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        for (Block block : new ArrayList<>(e.blockList())) {
            Chunk chunk = block.getLocation().getChunk();
            String chunkCord = plugin.getPU().formatChunkLocation(chunk);
            if (cache.isChunkClaimed(chunkCord)) {
                if (!cache.canChunkExplode(chunkCord)) e.blockList().remove(block);
            } else if (cache.isAdminChunk(chunkCord)) {
                if (!cache.canAdminChunkExplode(chunkCord)) e.blockList().remove(block);
            }
        }
    }

    //piston move handler
    @EventHandler
    public void onPistonMove(BlockPistonExtendEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        //check piston location
        Chunk chunk = e.getBlock().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);
        if (!cache.isChunkClaimed(chunkCord)) {
            e.setCancelled(true);
            return;
        }

        if (cache.isAdminChunk(chunkCord)) return;

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
        Cache cache = plugin.getCache();

        if (e.getEntity() instanceof Monster) {

            Chunk chunk = e.getEntity().getLocation().getChunk();
            String chunkCord = plugin.getPU().formatChunkLocation(chunk);

            if (cache.isChunkClaimed(chunkCord)) {
                if (!cache.canChunkSpawnMonsters(chunkCord)) e.setCancelled(true);
            } else if (cache.isAdminChunk(chunkCord)) {
                if (!cache.canAdminChunkSpawnMonsters(chunkCord)) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        Chunk chunk = e.getEntity().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (cache.isChunkClaimed(chunkCord) || cache.isAdminChunk(chunkCord)) {
            e.getDrops().clear();
            e.setKeepInventory(true);
            e.setKeepLevel(true);
        }
    }

    //auto claim handler
    @EventHandler
    public void onChunkChange(PlayerMoveEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();

        if (plugin.getData().isPlayerAutoClaiming(e.getPlayer().getUniqueId())) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();

            Chunk chunk = player.getLocation().getChunk();
            String chunkCord = plugin.getPU().formatChunkLocation(chunk);

            if (!cache.isChunkClaimed(chunkCord)) {

                if (cache.isAdminChunk(chunkCord)) {
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

                int playerClaimAmount = cache.getClaimedAmount(uuid);
                int playerMaxClaims = cache.getPlayerMaxClaimAmount(player);

                if (playerClaimAmount < playerMaxClaims) {
                    playerClaimAmount++;
                    cache.claimChunk(chunkCord, uuid);
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
