package lee.code.chunks.listeners;

import io.papermc.paper.event.block.BlockBreakBlockEvent;
import lee.code.chunks.Data;
import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.database.CacheManager;
import lee.code.chunks.lists.*;
import lee.code.chunks.lists.chunksettings.*;
import lee.code.core.util.bukkit.BukkitUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkListener implements Listener {

    private void warnMessage(Player player, boolean trusted, UUID owner, ChunkWarning warning) {
        Component messageType;
        String messageTrusted;

        if (trusted) messageTrusted = Lang.TRUE.getString(null);
        else messageTrusted = Lang.FALSE.getString(null);
        String name = Bukkit.getOfflinePlayer(owner).getName();

        messageType = switch (warning) {
            case BUILD -> Lang.ERROR_NO_CLAIM_PERMISSION.getComponent(new String[] { messageTrusted, Lang.ITEM_SETTINGS_BUILD_NAME.getString(new String[]{ Lang.FALSE.getString(null)}), name });
            case BREAK -> Lang.ERROR_NO_CLAIM_PERMISSION.getComponent(new String[] { messageTrusted, Lang.ITEM_SETTINGS_BREAK_NAME.getString(new String[]{Lang.FALSE.getString(null)}), name });
            case INTERACT -> Lang.ERROR_NO_CLAIM_PERMISSION.getComponent(new String[] { messageTrusted, Lang.ITEM_SETTINGS_INTERACT_NAME.getString(new String[]{Lang.FALSE.getString(null)}), name });
            case PVE -> Lang.ERROR_NO_CLAIM_PERMISSION.getComponent(new String[] { messageTrusted, Lang.ITEM_SETTINGS_PVE_NAME.getString(new String[]{Lang.FALSE.getString(null)}), name });
            case PORTAL -> Lang.ERROR_NO_CLAIM_PERMISSION.getComponent(new String[] { messageTrusted, Lang.ITEM_SETTINGS_PORTAL_NAME.getString(new String[]{Lang.FALSE.getString(null)}), name });
            case ADMIN -> Lang.ERROR_NO_CLAIM_PERMISSION_ADMIN_CHUNK.getComponent(null);
            default -> Component.text("ERROR");
        };
        player.sendActionBar(messageType);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        CacheManager cacheManager = plugin.getCacheManager();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        Chunk chunk = e.getBlock().getLocation().getChunk();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);

        if (!data.hasAdminBypass(uuid)) {
            if (cacheManager.isChunkClaimed(chunkCord)) {
                if (!cacheManager.isChunkOwner(chunkCord, uuid)) {
                    UUID owner = cacheManager.getChunkOwnerUUID(chunkCord);
                    //chunk trusted check
                    if (cacheManager.isChunkTrusted(chunkCord, uuid)) {
                        if (!cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.BREAK, chunkCord)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, ChunkWarning.BREAK);
                        }
                        //global trusted check
                    } else if (cacheManager.isGlobalTrusted(owner, uuid)) {
                        if (!cacheManager.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting.BREAK, owner)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, ChunkWarning.BREAK);
                        }
                    } else {
                        e.setCancelled(true);
                        warnMessage(player, false, owner, ChunkWarning.BREAK);
                    }
                }
            } else if (cacheManager.isAdminChunk(chunkCord)) {
                if (!cacheManager.canAdminChunkSetting(AdminChunkSetting.BREAK, chunkCord)) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHangingEntityBreak(HangingBreakByEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        CacheManager cacheManager = plugin.getCacheManager();

        Chunk chunk = e.getEntity().getLocation().getChunk();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);

        if (cacheManager.isChunkClaimed(chunkCord)) {

            if (e.getRemover() instanceof Player player) {
                UUID uuid = player.getUniqueId();

                if (!data.hasAdminBypass(uuid)) {
                    if (!cacheManager.isChunkOwner(chunkCord, uuid)) {
                        UUID owner = cacheManager.getChunkOwnerUUID(chunkCord);
                        //chunk trusted check
                        if (cacheManager.isChunkTrusted(chunkCord, uuid)) {
                            if (!cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.BREAK, chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarning.BREAK);
                            }
                            //global trusted check
                        } else if (cacheManager.isGlobalTrusted(owner, uuid)) {
                            if (!cacheManager.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting.BREAK, owner)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarning.BREAK);
                            }
                        } else {
                            e.setCancelled(true);
                            warnMessage(player, false, owner, ChunkWarning.BREAK);
                        }
                    }
                }
            } else if (e.getCause().equals(HangingBreakEvent.RemoveCause.EXPLOSION)) {
                e.setCancelled(true);
            }
        } else if (cacheManager.isAdminChunk(chunkCord)) {
            if (e.getRemover() instanceof Player player) {
                UUID uuid = player.getUniqueId();
                if (!data.hasAdminBypass(uuid)) {
                    if (!cacheManager.canAdminChunkSetting(AdminChunkSetting.BREAK, chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onVehicleBreak(VehicleDestroyEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        CacheManager cacheManager = plugin.getCacheManager();

        Chunk chunk = e.getVehicle().getLocation().getChunk();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);

        if (cacheManager.isChunkClaimed(chunkCord)) {
            if (e.getAttacker() instanceof Player player) {
                UUID uuid = player.getUniqueId();
                UUID owner = cacheManager.getChunkOwnerUUID(chunkCord);
                if (!data.hasAdminBypass(uuid)) {
                    if (!cacheManager.isChunkOwner(chunkCord, uuid)) {
                        if (cacheManager.isChunkTrusted(chunkCord, uuid)) {
                            if (!cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.BREAK, chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarning.BREAK);
                            }
                        } else if (cacheManager.isGlobalTrusted(owner, uuid)) {
                            if (!cacheManager.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting.BREAK, owner)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarning.BREAK);
                            }
                        } else {
                            e.setCancelled(true);
                            warnMessage(player, false, owner, ChunkWarning.BREAK);
                        }
                    }
                }
            }
        } else if (cacheManager.isAdminChunk(chunkCord)) {
            if (e.getAttacker() instanceof Player player) {
                UUID uuid = player.getUniqueId();
                if (!data.hasAdminBypass(uuid)) {
                    if (!cacheManager.canAdminChunkSetting(AdminChunkSetting.BREAK, chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        CacheManager cacheManager = plugin.getCacheManager();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Chunk chunk = e.getBlock().getLocation().getChunk();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);

        if (!data.hasAdminBypass(uuid)) {
            if (cacheManager.isChunkClaimed(chunkCord)) {
                if (!cacheManager.isChunkOwner(chunkCord, uuid)) {
                    UUID owner = cacheManager.getChunkOwnerUUID(chunkCord);
                    //chunk trusted check
                    if (cacheManager.isChunkTrusted(chunkCord, uuid)) {
                        if (!cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.BUILD, chunkCord)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, ChunkWarning.BUILD);
                        }
                        //global trusted check
                    } else if (cacheManager.isGlobalTrusted(owner, uuid)) {
                        if (!cacheManager.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting.BUILD, owner)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, ChunkWarning.BUILD);
                        }
                    } else {
                        e.setCancelled(true);
                        warnMessage(player, false, owner, ChunkWarning.BUILD);
                    }
                }
            } else if (cacheManager.isAdminChunk(chunkCord)) {
                if (!cacheManager.canAdminChunkSetting(AdminChunkSetting.BUILD, chunkCord)) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        CacheManager cacheManager = plugin.getCacheManager();

        if (e.hasBlock()) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            Block block = e.getClickedBlock();

            if (block != null) {
                BlockState blockState = block.getState();
                Chunk chunk = e.getClickedBlock().getLocation().getChunk();
                String chunkCord = plugin.getPU().serializeChunkLocation(chunk);
                boolean isSign = blockState instanceof Sign;

                if (!data.hasAdminBypass(uuid)) {
                    if (cacheManager.isChunkClaimed(chunkCord)) {
                        if (!cacheManager.isChunkOwner(chunkCord, uuid)) {
                            UUID owner = cacheManager.getChunkOwnerUUID(chunkCord);
                            if (e.getClickedBlock().getType().isInteractable() || e.getAction() == Action.PHYSICAL) {
                                //chunk trusted check
                                if (cacheManager.isChunkTrusted(chunkCord, uuid)) {
                                    if (!cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.INTERACT, chunkCord)) {
                                        e.setCancelled(true);
                                        if (!isSign) warnMessage(player, true, owner, ChunkWarning.INTERACT);
                                    }
                                    //global trusted check
                                } else if (cacheManager.isGlobalTrusted(owner, uuid)) {
                                    if (!cacheManager.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting.INTERACT, owner)) {
                                        e.setCancelled(true);
                                        if (!isSign) warnMessage(player, true, owner, ChunkWarning.INTERACT);
                                    }
                                } else {
                                    e.setCancelled(true);
                                    if (!isSign) warnMessage(player, false, owner, ChunkWarning.INTERACT);
                                }
                            } else {
                                //chunk trusted check
                                if (cacheManager.isChunkTrusted(chunkCord, uuid)) {
                                    if (!cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.BUILD, chunkCord)) {
                                        e.setCancelled(true);
                                        warnMessage(player, true, owner, ChunkWarning.BUILD);
                                    }
                                    //global trusted check
                                } else if (cacheManager.isGlobalTrusted(owner, uuid)) {
                                    if (!cacheManager.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting.BUILD, owner)) {
                                        e.setCancelled(true);
                                        warnMessage(player, true, owner, ChunkWarning.BUILD);
                                    }
                                } else {
                                    e.setCancelled(true);
                                    warnMessage(player, false, owner, ChunkWarning.BUILD);
                                }
                            }
                        }
                    } else if (cacheManager.isAdminChunk(chunkCord)) {
                        if (block.getType().isInteractable() || e.getAction() == Action.PHYSICAL) {
                            if (!cacheManager.canAdminChunkSetting(AdminChunkSetting.INTERACT, chunkCord)) {
                                e.setCancelled(true);
                                if (!isSign) warnMessage(player, false, uuid, ChunkWarning.ADMIN);
                            }
                        } else if (!cacheManager.canAdminChunkSetting(AdminChunkSetting.BUILD, chunkCord)) {
                            e.setCancelled(true);
                            if (!isSign) warnMessage(player, false, uuid, ChunkWarning.ADMIN);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onArmorStandInteract(PlayerInteractAtEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        CacheManager cacheManager = plugin.getCacheManager();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (e.getRightClicked() instanceof ArmorStand) {

            Chunk chunk = e.getRightClicked().getLocation().getChunk();
            String chunkCord = plugin.getPU().serializeChunkLocation(chunk);

            if (!data.hasAdminBypass(uuid)) {
                if (cacheManager.isChunkClaimed(chunkCord)) {
                    if (!cacheManager.isChunkOwner(chunkCord, uuid)) {
                        UUID owner = cacheManager.getChunkOwnerUUID(chunkCord);
                        //chunk trusted check
                        if (cacheManager.isChunkTrusted(chunkCord, uuid)) {
                            if (!cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.INTERACT, chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarning.INTERACT);
                            }
                            //global trusted check
                        } else if (cacheManager.isGlobalTrusted(owner, uuid)) {
                            if (!cacheManager.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting.INTERACT, owner)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarning.INTERACT);
                            }
                        } else {
                            e.setCancelled(true);
                            warnMessage(player, false, owner, ChunkWarning.INTERACT);
                        }
                    }
                } else if (cacheManager.isAdminChunk(chunkCord)) {
                    if (!cacheManager.canAdminChunkSetting(AdminChunkSetting.INTERACT, chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        CacheManager cacheManager = plugin.getCacheManager();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        Chunk chunk = e.getRightClicked().getLocation().getChunk();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);

        if (!(e.getRightClicked() instanceof Player)) {
            if (!data.hasAdminBypass(uuid)) {
                if (cacheManager.isChunkClaimed(chunkCord)) {
                    if (!cacheManager.isChunkOwner(chunkCord, uuid)) {
                        UUID owner = cacheManager.getChunkOwnerUUID(chunkCord);
                        //chunk trusted check
                        if (cacheManager.isChunkTrusted(chunkCord, uuid)) {
                            if (!cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.INTERACT, chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarning.INTERACT);
                            }
                            //global trusted check
                        } else if (cacheManager.isGlobalTrusted(owner, uuid)) {
                            if (!cacheManager.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting.INTERACT, owner)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarning.INTERACT);
                            }
                        } else {
                            e.setCancelled(true);
                            warnMessage(player, false, owner, ChunkWarning.INTERACT);
                        }
                    }
                } else if (cacheManager.isAdminChunk(chunkCord)) {
                    if (!cacheManager.canAdminChunkSetting(AdminChunkSetting.INTERACT, chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onProjectileTeleport(PlayerTeleportEvent e) {
        if (e.getCause().equals(PlayerTeleportEvent.TeleportCause.ENDER_PEARL) || e.getCause().equals(PlayerTeleportEvent.TeleportCause.CHORUS_FRUIT)) {
            GoldmanChunks plugin = GoldmanChunks.getPlugin();
            PU pu = plugin.getPU();
            Data data = plugin.getData();
            CacheManager cacheManager = plugin.getCacheManager();

            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            Chunk chunk = e.getTo().getChunk();
            String chunkCord = pu.serializeChunkLocation(chunk);

            if (cacheManager.isChunkClaimed(chunkCord)) {
                if (!data.hasAdminBypass(uuid)) {
                    if (!cacheManager.isChunkOwner(chunkCord, uuid)) {
                        UUID owner = cacheManager.getChunkOwnerUUID(chunkCord);
                        if (cacheManager.isGlobalTrusted(owner, uuid)) {
                            if (!cacheManager.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting.INTERACT, owner)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarning.INTERACT);
                            }
                        } else if (cacheManager.isChunkTrusted(chunkCord, uuid)) {
                            if (!cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.INTERACT, chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarning.INTERACT);
                            }
                        } else {
                            e.setCancelled(true);
                            warnMessage(player, false, owner, ChunkWarning.INTERACT);
                        }
                    }
                }
            } else if (cacheManager.isAdminChunk(chunkCord)) {
                if (!data.hasAdminBypass(uuid)) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        PU pu = plugin.getPU();
        Data data = plugin.getData();
        CacheManager cacheManager = plugin.getCacheManager();

        Entity entity = e.getEntity();
        Chunk chunk = entity.getLocation().getChunk();
        String chunkCord = pu.serializeChunkLocation(chunk);

        if (cacheManager.isChunkClaimed(chunkCord)) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) || e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                e.setCancelled(true);
                return;
            }
            //pve
            if (e.getDamager() instanceof Player player && !(e.getEntity() instanceof Player)) {
                UUID uuid = player.getUniqueId();
                if (!data.hasAdminBypass(uuid)) {
                    if (!data.getHostileEntities().contains(entity.getType()) || entity.customName() != null) {
                        UUID owner = cacheManager.getChunkOwnerUUID(chunkCord);
                        if (!cacheManager.isChunkOwner(chunkCord, uuid)) {
                            if (cacheManager.isGlobalTrusted(owner, uuid)) {
                                if (!cacheManager.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting.PVE, owner)) {
                                    e.setCancelled(true);
                                    warnMessage(player, true, owner, ChunkWarning.PVE);
                                }
                            } else if (cacheManager.isChunkTrusted(chunkCord, uuid)) {
                                if (!cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.PVE, chunkCord)) {
                                    e.setCancelled(true);
                                    warnMessage(player, true, owner, ChunkWarning.PVE);
                                }
                            } else {
                                e.setCancelled(true);
                                warnMessage(player, false, owner, ChunkWarning.PVE);
                            }
                        }
                    }
                }
            } else if (e.getDamager() instanceof Projectile projectile) {
                //projectile pve
                if (projectile.getShooter() instanceof Player player) {
                    UUID uuid = player.getUniqueId();
                    if (!data.hasAdminBypass(uuid)) {
                        if (!data.getHostileEntities().contains(entity.getType())) {
                            UUID owner = cacheManager.getChunkOwnerUUID(chunkCord);
                            if (!cacheManager.isChunkOwner(chunkCord, uuid)) {
                                if (cacheManager.isGlobalTrusted(owner, uuid)) {
                                    if (!cacheManager.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSetting.PVE, owner)) {
                                        e.setCancelled(true);
                                        warnMessage(player, true, owner, ChunkWarning.PVE);
                                    }
                                } else if (cacheManager.isChunkTrusted(chunkCord, uuid)) {
                                    if (!cacheManager.canChunkTrustedSetting(ChunkTrustedSetting.PVE, chunkCord)) {
                                        e.setCancelled(true);
                                        warnMessage(player, true, owner, ChunkWarning.PVE);
                                    }
                                } else {
                                    e.setCancelled(true);
                                    warnMessage(player, false, owner, ChunkWarning.PVE);
                                }
                            }
                        }
                    }
                }
            }
            //admin chunk claim
        } else if (cacheManager.isAdminChunk(chunkCord)) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) || e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                e.setCancelled(true);
                return;
            }
            //evp
            if (!(e.getDamager() instanceof Player) && entity instanceof Player) {
                UUID uuid = entity.getUniqueId();
                if (!data.hasAdminBypass(uuid)) {
                    if (!cacheManager.canAdminChunkSetting(AdminChunkSetting.PVE, chunkCord)) e.setCancelled(true);
                }
                //pve
            } else if (e.getDamager() instanceof Player && !(entity instanceof Player) && !data.getHostileEntities().contains(entity.getType())) {
                UUID uuid = e.getDamager().getUniqueId();
                if (!data.hasAdminBypass(uuid)) {
                    if (!cacheManager.canAdminChunkSetting(AdminChunkSetting.PVE, chunkCord)) e.setCancelled(true);
                }
            } else if (e.getDamager() instanceof Projectile projectile) {
                //projectile
                if (projectile.getShooter() instanceof Player player) {
                    UUID uuid = player.getUniqueId();
                    if (!data.hasAdminBypass(uuid)) {
                        if (!cacheManager.canAdminChunkSetting(AdminChunkSetting.PVE, chunkCord)) e.setCancelled(true);
                    }
                } else e.setCancelled(true);
            }
        }
    }

    //explode handler
    @EventHandler
    public void onExplodeEvent(EntityExplodeEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();

        for (Block block : new ArrayList<>(e.blockList())) {
            Chunk chunk = block.getLocation().getChunk();
            String chunkCord = plugin.getPU().serializeChunkLocation(chunk);
            if (cacheManager.isChunkClaimed(chunkCord)) {
                if (!cacheManager.canChunkSetting(ChunkSetting.EXPLOSIONS, chunkCord)) e.blockList().remove(block);
            } else if (cacheManager.isAdminChunk(chunkCord)) {
                if (!cacheManager.canAdminChunkSetting(AdminChunkSetting.EXPLOSIONS, chunkCord)) e.blockList().remove(block);
            }
        }
    }

    //piston move handler
    @EventHandler
    public void onPistonMove(BlockPistonExtendEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        Data data = plugin.getData();
        PU pu = plugin.getPU();

        //check piston location
        Chunk chunk = e.getBlock().getLocation().getChunk();
        String chunkCord = pu.serializeChunkLocation(chunk);

        if (data.getWhitelistedWorlds().contains(chunk.getWorld().getName())) {
            if (!cacheManager.isChunkClaimed(chunkCord)) {
                e.setCancelled(true);
                return;
            }

            if (cacheManager.isAdminChunk(chunkCord)) return;

            //check piston moved blocks
            for (Block block : new ArrayList<>(e.getBlocks())) {
                Chunk movedBlockChunk = block.getLocation().getChunk();
                String movedBlockChunkCords = pu.serializeChunkLocation(movedBlockChunk);
                if (!cacheManager.isChunkClaimed(movedBlockChunkCords)) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    //monster spawn handler
    @EventHandler
    public void onMonsterSpawn(CreatureSpawnEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        if (!e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
            if (plugin.getData().getHostileEntities().contains(e.getEntity().getType())) {
                Chunk chunk = e.getEntity().getLocation().getChunk();
                String chunkCord = plugin.getPU().serializeChunkLocation(chunk);

                if (cacheManager.isChunkClaimed(chunkCord)) {
                    if (!cacheManager.canChunkSetting(ChunkSetting.MONSTERS, chunkCord)) e.setCancelled(true);
                } else if (cacheManager.isAdminChunk(chunkCord)) {
                    if (!cacheManager.canAdminChunkSetting(AdminChunkSetting.MONSTERS, chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();

        Chunk chunk = e.getPlayer().getLocation().getChunk();
        String chunkCord = plugin.getPU().serializeChunkLocation(chunk);
        if (cacheManager.isChunkClaimed(chunkCord) || cacheManager.isAdminChunk(chunkCord)) {
            e.getDrops().clear();
            e.setKeepInventory(true);
            e.setShouldDropExperience(false);
            e.setKeepLevel(true);
        }
    }

    //auto claim handler
    @EventHandler
    public void onAutoClaim(PlayerMoveEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        PU pu = plugin.getPU();
        Data data = plugin.getData();
        UUID uuid = e.getPlayer().getUniqueId();

        if (data.isPlayerAutoClaiming(uuid)) {
            Player player = e.getPlayer();

            Chunk chunk = player.getLocation().getChunk();
            String chunkCord = pu.serializeChunkLocation(chunk);

            if (!cacheManager.isChunkClaimed(chunkCord)) {
                if (!cacheManager.isAdminChunk(chunkCord)) {
                    List<String> chunksAroundPlayer = pu.getChunksAroundPlayer(player.getLocation());
                    String lastChunkClaim = data.getPlayerLastAutoClaim(uuid);
                    if (chunksAroundPlayer.contains(lastChunkClaim)) {

                        int playerClaimAmount = cacheManager.getClaimedAmount(uuid);
                        int playerMaxClaims = cacheManager.getPlayerMaxClaimAmount(uuid);

                        if (playerClaimAmount < playerMaxClaims) {
                            playerClaimAmount++;
                            cacheManager.claimChunk(chunkCord, uuid);
                            data.setPlayerAutoClaim(uuid,chunkCord);
                            player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_CLAIM_SUCCESSFUL.getString(new String[]{chunkCord, BukkitUtils.parseValue(playerClaimAmount), BukkitUtils.parseValue(playerMaxClaims)}));
                            pu.renderChunkBorder(player, chunk, RenderTypes.CLAIM);
                        } else {
                            data.removePlayerAutoClaim(uuid);
                            player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_CLAIM_MAXED.getString(new String[] { BukkitUtils.parseValue(playerClaimAmount), BukkitUtils.parseValue(playerMaxClaims) }));
                        }
                    } else {
                        data.removePlayerAutoClaim(uuid);
                        player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_AUTO_CLAIM.getString(null));
                    }
                } else {
                    data.removePlayerAutoClaim(uuid);
                    player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_ADMIN_CLAIMED.getString(null));
                }
            } else if (!data.getPlayerLastAutoClaim(uuid).equals(chunkCord)) data.setPlayerAutoClaim(uuid, chunkCord);
        }
    }

    @EventHandler
    public void onChunkFlying(PlayerMoveEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        UUID uuid = e.getPlayer().getUniqueId();

        if (cacheManager.isChunkFlying(uuid)) {
            Player player = e.getPlayer();
            Chunk chunk = player.getLocation().getChunk();
            String chunkCord = plugin.getPU().serializeChunkLocation(chunk);

            if (cacheManager.isChunkClaimed(chunkCord)) {
                UUID ownerUUID = cacheManager.getChunkOwnerUUID(chunkCord);
                if (!cacheManager.isChunkOwner(chunkCord, uuid) && !cacheManager.isChunkTrusted(chunkCord, uuid) && !cacheManager.isGlobalTrusted(ownerUUID, uuid)) toggleChunkFly(player, ChunkWarning.FLY_OUTSIDE);
            } else toggleChunkFly(player, ChunkWarning.FLY_OUTSIDE);
        }
    }

    //portal claim check
    @EventHandler
    public void onPortalUse(PlayerPortalEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        CacheManager cacheManager = plugin.getCacheManager();
        PU pu = plugin.getPU();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Chunk chunk = e.getTo().getChunk();
        String chunkCord = pu.serializeChunkLocation(chunk);

        if (!e.getTo().getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            if (!data.hasAdminBypass(uuid)) {
                if (cacheManager.isChunkClaimed(chunkCord)) {
                    UUID ownerUUID = cacheManager.getChunkOwnerUUID(chunkCord);
                    if (!cacheManager.isChunkOwner(chunkCord, uuid)) {
                        if (!cacheManager.isChunkTrusted(chunkCord, uuid) && !cacheManager.isGlobalTrusted(ownerUUID, uuid)) {
                            e.setCancelled(true);
                            warnMessage(player, false, ownerUUID, ChunkWarning.PORTAL);
                        }
                    }
                } else if (cacheManager.isAdminChunk(chunkCord)) {
                    e.setCancelled(true);
                    warnMessage(player, false, uuid, ChunkWarning.ADMIN);
                }
            }
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onPVPChunkFlying(EntityDamageByEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        if (!e.isCancelled()) {
            if (e.getDamager() instanceof Player attacker && e.getEntity() instanceof Player attacked) {
                if (cacheManager.isChunkFlying(attacker.getUniqueId())) toggleChunkFly(attacker, ChunkWarning.FLY_PVP);
                if (cacheManager.isChunkFlying(attacked.getUniqueId())) toggleChunkFly(attacked, ChunkWarning.FLY_PVP);
            }
        }
    }

    private void toggleChunkFly(Player player, ChunkWarning type) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        UUID uuid = player.getUniqueId();
        player.setAllowFlight(false);
        player.setFlying(false);
        cacheManager.setChunkFlying(uuid, false);
        player.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(20*15, 1));
        if (type.equals(ChunkWarning.FLY_OUTSIDE)) player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_FLY_OUTSIDE_OF_CLAIM.getComponent(null)));
        else if (type.equals(ChunkWarning.FLY_PVP)) player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_FLY_PVP.getComponent(null)));
    }

    @EventHandler
    public void onChunkBlockChange(EntityChangeBlockEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        CacheManager cacheManager = plugin.getCacheManager();
        PU pu = plugin.getPU();
        Entity entity = e.getEntity();
        if (entity instanceof Wither) {
            Chunk chunk = entity.getChunk();
            String chunkCord = pu.serializeChunkLocation(chunk);
            if (cacheManager.isChunkClaimed(chunkCord)) {
                if (!cacheManager.canChunkSetting(ChunkSetting.EXPLOSIONS, chunkCord)) {
                    e.setCancelled(true);
                }
            } else if (cacheManager.isAdminChunk(chunkCord)) {
                if (!cacheManager.canAdminChunkSetting(AdminChunkSetting.EXPLOSIONS, chunkCord)) e.setCancelled(true);
            }
        }
    }
}
