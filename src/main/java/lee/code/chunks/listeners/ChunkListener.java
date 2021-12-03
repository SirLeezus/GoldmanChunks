package lee.code.chunks.listeners;

import lee.code.chunks.Data;
import lee.code.chunks.GoldmanChunks;
import lee.code.chunks.PU;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.*;
import lee.code.chunks.lists.chunksettings.*;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
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
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkListener implements Listener {

    private void warnMessage(Player player, boolean trusted, UUID owner, ChunkWarnings warning) {
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
            case PVP -> Lang.ERROR_PVP_DISABLED.getComponent(null);
            case ADMIN -> Lang.ERROR_NO_CLAIM_PERMISSION_ADMIN_CHUNK.getComponent(null);
            default -> Component.text("ERROR");
        };
        player.sendActionBar(messageType);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        Cache cache = plugin.getCache();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        Chunk chunk = e.getBlock().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (!data.hasAdminBypass(uuid)) {
            if (cache.isChunkClaimed(chunkCord)) {
                if (!cache.isChunkOwner(chunkCord, uuid)) {
                    UUID owner = cache.getChunkOwnerUUID(chunkCord);
                    //chunk trusted check
                    if (cache.isChunkTrusted(chunkCord, uuid)) {
                        if (!cache.canChunkTrustedSetting(ChunkTrustedSettings.BREAK, chunkCord)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, ChunkWarnings.BREAK);
                        }
                        //global trusted check
                    } else if (cache.isGlobalTrusted(owner, uuid)) {
                        if (!cache.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSettings.BREAK, owner)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, ChunkWarnings.BREAK);
                        }
                    } else {
                        e.setCancelled(true);
                        warnMessage(player, false, owner, ChunkWarnings.BREAK);
                    }
                }
            } else if (cache.isAdminChunk(chunkCord)) {
                if (!cache.canAdminChunkSetting(ChunkAdminSettings.BREAK, chunkCord)) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHangingEntityBreak(HangingBreakByEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        Cache cache = plugin.getCache();

        Chunk chunk = e.getEntity().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (cache.isChunkClaimed(chunkCord)) {

            if (e.getRemover() instanceof Player player) {
                UUID uuid = player.getUniqueId();

                if (!data.hasAdminBypass(uuid)) {
                    if (!cache.isChunkOwner(chunkCord, uuid)) {
                        UUID owner = cache.getChunkOwnerUUID(chunkCord);
                        //chunk trusted check
                        if (cache.isChunkTrusted(chunkCord, uuid)) {
                            if (!cache.canChunkTrustedSetting(ChunkTrustedSettings.BREAK, chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarnings.BREAK);
                            }
                            //global trusted check
                        } else if (cache.isGlobalTrusted(owner, uuid)) {
                            if (!cache.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSettings.BREAK, owner)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarnings.BREAK);
                            }
                        } else {
                            e.setCancelled(true);
                            warnMessage(player, false, owner, ChunkWarnings.BREAK);
                        }
                    }
                }
            } else if (e.getCause().equals(HangingBreakEvent.RemoveCause.EXPLOSION)) {
                e.setCancelled(true);
            }
        } else if (cache.isAdminChunk(chunkCord)) {
            if (e.getRemover() instanceof Player player) {
                UUID uuid = player.getUniqueId();
                if (!data.hasAdminBypass(uuid)) {
                    if (!cache.canAdminChunkSetting(ChunkAdminSettings.BREAK, chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onVehicleBreak(VehicleDestroyEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        Cache cache = plugin.getCache();

        Chunk chunk = e.getVehicle().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (cache.isChunkClaimed(chunkCord)) {
            if (e.getAttacker() instanceof Player player) {
                UUID uuid = player.getUniqueId();
                UUID owner = cache.getChunkOwnerUUID(chunkCord);
                if (!data.hasAdminBypass(uuid)) {
                    if (!cache.isChunkOwner(chunkCord, uuid)) {
                        if (cache.isChunkTrusted(chunkCord, uuid)) {
                            if (!cache.canChunkTrustedSetting(ChunkTrustedSettings.BREAK, chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarnings.BREAK);
                            }
                        } else if (cache.isGlobalTrusted(owner, uuid)) {
                            if (!cache.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSettings.BREAK, owner)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarnings.BREAK);
                            }
                        } else {
                            e.setCancelled(true);
                            warnMessage(player, false, owner, ChunkWarnings.BREAK);
                        }
                    }
                }
            } else if (!(e.getAttacker() instanceof Player)) {
                e.setCancelled(true);
            }
        } else if (cache.isAdminChunk(chunkCord)) {
            if (e.getAttacker() instanceof Player player) {
                UUID uuid = player.getUniqueId();
                if (!data.hasAdminBypass(uuid)) {
                    if (!cache.canAdminChunkSetting(ChunkAdminSettings.BREAK, chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBucketEmpty(PlayerBucketEmptyEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        Cache cache = plugin.getCache();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Chunk chunk = e.getBlock().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (!data.hasAdminBypass(uuid)) {
            if (cache.isChunkClaimed(chunkCord)) {
                if (!cache.isChunkOwner(chunkCord, uuid)) {
                    UUID owner = cache.getChunkOwnerUUID(chunkCord);
                    //chunk trusted check
                    if (cache.isChunkTrusted(chunkCord, uuid)) {
                        if (!cache.canChunkTrustedSetting(ChunkTrustedSettings.BUILD, chunkCord)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, ChunkWarnings.BUILD);
                        }
                        //global trusted check
                    } else if (cache.isGlobalTrusted(owner, uuid)) {
                        if (!cache.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSettings.BUILD, owner)) {
                            e.setCancelled(true);
                            warnMessage(player, true, owner, ChunkWarnings.BUILD);
                        }
                    } else {
                        e.setCancelled(true);
                        warnMessage(player, false, owner, ChunkWarnings.BUILD);
                    }
                }
            } else if (cache.isAdminChunk(chunkCord)) {
                if (!cache.canAdminChunkSetting(ChunkAdminSettings.BUILD, chunkCord)) e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        Cache cache = plugin.getCache();

        if (e.hasBlock()) {
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            Block block = e.getClickedBlock();

            if (block != null) {
                BlockState blockState = block.getState();
                Chunk chunk = e.getClickedBlock().getLocation().getChunk();
                String chunkCord = plugin.getPU().formatChunkLocation(chunk);
                boolean isSign = blockState instanceof Sign;

                if (!data.hasAdminBypass(uuid)) {
                    if (cache.isChunkClaimed(chunkCord)) {
                        if (!cache.isChunkOwner(chunkCord, uuid)) {
                            UUID owner = cache.getChunkOwnerUUID(chunkCord);
                            if (e.getClickedBlock().getType().isInteractable() || e.getAction() == Action.PHYSICAL) {
                                //chunk trusted check
                                if (cache.isChunkTrusted(chunkCord, uuid)) {
                                    if (!cache.canChunkTrustedSetting(ChunkTrustedSettings.INTERACT, chunkCord)) {
                                        e.setCancelled(true);
                                        if (!isSign) warnMessage(player, true, owner, ChunkWarnings.INTERACT);
                                    }
                                    //global trusted check
                                } else if (cache.isGlobalTrusted(owner, uuid)) {
                                    if (!cache.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSettings.INTERACT, owner)) {
                                        e.setCancelled(true);
                                        if (!isSign) warnMessage(player, true, owner, ChunkWarnings.INTERACT);
                                    }
                                } else {
                                    e.setCancelled(true);
                                    if (!isSign) warnMessage(player, false, owner, ChunkWarnings.INTERACT);
                                }
                            } else {
                                //chunk trusted check
                                if (cache.isChunkTrusted(chunkCord, uuid)) {
                                    if (!cache.canChunkTrustedSetting(ChunkTrustedSettings.BUILD, chunkCord)) {
                                        e.setCancelled(true);
                                        warnMessage(player, true, owner, ChunkWarnings.BUILD);
                                    }
                                    //global trusted check
                                } else if (cache.isGlobalTrusted(owner, uuid)) {
                                    if (!cache.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSettings.BUILD, owner)) {
                                        e.setCancelled(true);
                                        warnMessage(player, true, owner, ChunkWarnings.BUILD);
                                    }
                                } else {
                                    e.setCancelled(true);
                                    warnMessage(player, false, owner, ChunkWarnings.BUILD);
                                }
                            }
                        }
                    } else if (cache.isAdminChunk(chunkCord)) {
                        if (block.getType().isInteractable() || e.getAction() == Action.PHYSICAL) {
                            if (!cache.canAdminChunkSetting(ChunkAdminSettings.INTERACT, chunkCord)) {
                                e.setCancelled(true);
                                if (!isSign) warnMessage(player, false, uuid, ChunkWarnings.ADMIN);
                            }
                        } else if (!cache.canAdminChunkSetting(ChunkAdminSettings.BUILD, chunkCord)) {
                            e.setCancelled(true);
                            if (!isSign) warnMessage(player, false, uuid, ChunkWarnings.ADMIN);
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
        Cache cache = plugin.getCache();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        if (e.getRightClicked() instanceof ArmorStand) {

            Chunk chunk = e.getRightClicked().getLocation().getChunk();
            String chunkCord = plugin.getPU().formatChunkLocation(chunk);

            if (!data.hasAdminBypass(uuid)) {
                if (cache.isChunkClaimed(chunkCord)) {
                    if (!cache.isChunkOwner(chunkCord, uuid)) {
                        UUID owner = cache.getChunkOwnerUUID(chunkCord);
                        //chunk trusted check
                        if (cache.isChunkTrusted(chunkCord, uuid)) {
                            if (!cache.canChunkTrustedSetting(ChunkTrustedSettings.INTERACT, chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarnings.INTERACT);
                            }
                            //global trusted check
                        } else if (cache.isGlobalTrusted(owner, uuid)) {
                            if (!cache.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSettings.INTERACT, owner)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarnings.INTERACT);
                            }
                        } else {
                            e.setCancelled(true);
                            warnMessage(player, false, owner, ChunkWarnings.INTERACT);
                        }
                    }
                } else if (cache.isAdminChunk(chunkCord)) {
                    if (!cache.canAdminChunkSetting(ChunkAdminSettings.INTERACT, chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        Cache cache = plugin.getCache();

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        Chunk chunk = e.getRightClicked().getLocation().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (!(e.getRightClicked() instanceof Player)) {
            if (!data.hasAdminBypass(uuid)) {
                if (cache.isChunkClaimed(chunkCord)) {
                    if (!cache.isChunkOwner(chunkCord, uuid)) {
                        UUID owner = cache.getChunkOwnerUUID(chunkCord);
                        //chunk trusted check
                        if (cache.isChunkTrusted(chunkCord, uuid)) {
                            if (!cache.canChunkTrustedSetting(ChunkTrustedSettings.INTERACT, chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarnings.INTERACT);
                            }
                            //global trusted check
                        } else if (cache.isGlobalTrusted(owner, uuid)) {
                            if (!cache.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSettings.INTERACT, owner)) {
                                e.setCancelled(true);
                                warnMessage(player, true, owner, ChunkWarnings.INTERACT);
                            }
                        } else {
                            e.setCancelled(true);
                            warnMessage(player, false, owner, ChunkWarnings.INTERACT);
                        }
                    }
                } else if (cache.isAdminChunk(chunkCord)) {
                    if (!cache.canAdminChunkSetting(ChunkAdminSettings.INTERACT, chunkCord)) e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        PU pu = plugin.getPU();
        Data data = plugin.getData();
        Cache cache = plugin.getCache();

        Entity entity = e.getEntity();
        Chunk chunk = entity.getLocation().getChunk();
        String chunkCord = pu.formatChunkLocation(chunk);

        if (cache.isChunkClaimed(chunkCord)) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) || e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                e.setCancelled(true);
                return;
            }
            //pvp
            if (e.getDamager() instanceof Player player) {
                UUID uuid = player.getUniqueId();
                if (!data.hasAdminBypass(uuid)) {
                    if (entity instanceof Player) {
                        if (!cache.canChunkSetting(ChunkSettings.PVP, chunkCord)) {
                            e.setCancelled(true);
                            warnMessage(player, false, uuid, ChunkWarnings.PVP);
                        } else if (cache.isChunkFlying(uuid)) toggleChunkFly(player, ChunkWarnings.FLY_PVP);
                        //pve
                    } else if (!pu.getHostileEntities().contains(entity.getType())) {
                        UUID owner = cache.getChunkOwnerUUID(chunkCord);
                        if (!cache.isChunkOwner(chunkCord, uuid)) {
                            if (cache.isGlobalTrusted(owner, uuid)) {
                                if (!cache.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSettings.PVE, owner)) {
                                    e.setCancelled(true);
                                    warnMessage(player, true, owner, ChunkWarnings.PVE);
                                }
                            } else if (cache.isChunkTrusted(chunkCord, uuid)) {
                                if (!cache.canChunkTrustedSetting(ChunkTrustedSettings.PVE, chunkCord)) {
                                    e.setCancelled(true);
                                    warnMessage(player, true, owner, ChunkWarnings.PVE);
                                }
                            } else {
                                e.setCancelled(true);
                                warnMessage(player, false, owner, ChunkWarnings.PVE);
                            }
                        }
                    }
                }
            } else if (e.getDamager() instanceof Projectile projectile) {

                //projectile pvp & pve
                if (projectile.getShooter() instanceof Player player) {
                    UUID uuid = player.getUniqueId();

                    if (!data.hasAdminBypass(uuid)) {
                        //pvp
                        if (entity instanceof Player) {
                            if (!cache.canChunkSetting(ChunkSettings.PVP, chunkCord)) {
                                e.setCancelled(true);
                                warnMessage(player, false, uuid, ChunkWarnings.PVP);
                            } else if (cache.isChunkFlying(uuid)) toggleChunkFly(player, ChunkWarnings.FLY_PVP);
                            //pve
                        } else if (!pu.getHostileEntities().contains(entity.getType())) {
                            UUID owner = cache.getChunkOwnerUUID(chunkCord);
                            if (!cache.isChunkOwner(chunkCord, uuid)) {
                                if (cache.isGlobalTrusted(owner, uuid)) {
                                    if (!cache.canChunkTrustedGlobalSetting(ChunkTrustedGlobalSettings.PVE, owner)) {
                                        e.setCancelled(true);
                                        warnMessage(player, true, owner, ChunkWarnings.PVE);
                                    }
                                } else if (cache.isChunkTrusted(chunkCord, uuid)) {
                                    if (!cache.canChunkTrustedSetting(ChunkTrustedSettings.PVE, chunkCord)) {
                                        e.setCancelled(true);
                                        warnMessage(player, true, owner, ChunkWarnings.PVE);
                                    }
                                } else {
                                    e.setCancelled(true);
                                    warnMessage(player, false, owner, ChunkWarnings.PVE);
                                }
                            }
                        }
                    }
                }
            }
            //admin chunk claim
        } else if (cache.isAdminChunk(chunkCord)) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) || e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                e.setCancelled(true);
                return;
            }
            //pvp
            if (entity instanceof Player && e.getDamager() instanceof Player) {
                UUID uuid = e.getDamager().getUniqueId();
                if (!data.hasAdminBypass(uuid)) {
                    if (!cache.canAdminChunkSetting(ChunkAdminSettings.PVP, chunkCord)) e.setCancelled(true);
                }
                //evp
            } else if (!(e.getDamager() instanceof Player) && entity instanceof Player) {
                UUID uuid = entity.getUniqueId();
                if (!data.hasAdminBypass(uuid)) {
                    if (!cache.canAdminChunkSetting(ChunkAdminSettings.PVE, chunkCord)) e.setCancelled(true);
                }
                //pve
            } else if (e.getDamager() instanceof Player && !(entity instanceof Player) && !pu.getHostileEntities().contains(entity.getType())) {
                UUID uuid = e.getDamager().getUniqueId();
                if (!data.hasAdminBypass(uuid)) {
                    if (!cache.canAdminChunkSetting(ChunkAdminSettings.PVE, chunkCord)) e.setCancelled(true);
                }
            } else if (e.getDamager() instanceof Projectile projectile) {
                //projectile
                if (projectile.getShooter() instanceof Player player) {
                    UUID uuid = player.getUniqueId();
                    if (!data.hasAdminBypass(uuid)) {
                        if (!cache.canAdminChunkSetting(ChunkAdminSettings.PVE, chunkCord)) e.setCancelled(true);
                    }
                } else e.setCancelled(true);
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
                if (!cache.canChunkSetting(ChunkSettings.EXPLOSIONS, chunkCord)) e.blockList().remove(block);
            } else if (cache.isAdminChunk(chunkCord)) {
                if (!cache.canAdminChunkSetting(ChunkAdminSettings.EXPLOSIONS, chunkCord)) e.blockList().remove(block);
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
            if (!cache.isChunkClaimed(movedBlockChunkCords)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    //monster spawn handler
    @EventHandler
    public void onMonsterSpawn(CreatureSpawnEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        PU pu = plugin.getPU();
        Cache cache = plugin.getCache();
        if (!e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)) {
            if (pu.getHostileEntities().contains(e.getEntity().getType())) {
                Chunk chunk = e.getEntity().getLocation().getChunk();
                String chunkCord = pu.formatChunkLocation(chunk);

                if (cache.isChunkClaimed(chunkCord)) {
                    if (!cache.canChunkSetting(ChunkSettings.MONSTERS, chunkCord)) e.setCancelled(true);
                } else if (cache.isAdminChunk(chunkCord)) {
                    if (!cache.canAdminChunkSetting(ChunkAdminSettings.MONSTERS, chunkCord)) e.setCancelled(true);
                }
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
            e.setDroppedExp(0);
            e.setKeepInventory(true);
            e.setKeepLevel(true);
        }
    }

    //auto claim handler
    @EventHandler
    public void onAutoClaim(PlayerMoveEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        Data data = plugin.getData();
        UUID uuid = e.getPlayer().getUniqueId();

        if (data.isPlayerAutoClaiming(uuid)) {
            Player player = e.getPlayer();

            Chunk chunk = player.getLocation().getChunk();
            String chunkCord = plugin.getPU().formatChunkLocation(chunk);

            if (!cache.isChunkClaimed(chunkCord)) {
                if (!cache.isAdminChunk(chunkCord)) {
                    List<String> chunksAroundPlayer = plugin.getPU().getChunksAroundPlayer(player);
                    String lastChunkClaim = data.getPlayerLastAutoClaim(uuid);
                    if (chunksAroundPlayer.contains(lastChunkClaim)) {

                        int playerClaimAmount = cache.getClaimedAmount(uuid);
                        int playerMaxClaims = cache.getPlayerMaxClaimAmount(uuid);

                        if (playerClaimAmount < playerMaxClaims) {
                            playerClaimAmount++;
                            cache.claimChunk(chunkCord, uuid);
                            data.setPlayerAutoClaim(uuid,chunkCord);
                            player.sendMessage(Lang.PREFIX.getString(null) + Lang.COMMAND_CLAIM_SUCCESSFUL.getString(new String[]{chunkCord, plugin.getPU().formatAmount(playerClaimAmount), plugin.getPU().formatAmount(playerMaxClaims)}));
                            plugin.getPU().renderChunkBorder(player, chunk, RenderTypes.CLAIM);
                        } else {
                            data.removePlayerAutoClaim(uuid);
                            player.sendMessage(Lang.PREFIX.getString(null) + Lang.ERROR_COMMAND_CLAIM_MAXED.getString(new String[] { plugin.getPU().formatAmount(playerClaimAmount), plugin.getPU().formatAmount(playerMaxClaims) }));
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
        Cache cache = plugin.getCache();
        UUID uuid = e.getPlayer().getUniqueId();

        if (cache.isChunkFlying(uuid)) {
            Player player = e.getPlayer();
            Chunk chunk = player.getLocation().getChunk();
            String chunkCord = plugin.getPU().formatChunkLocation(chunk);

            if (cache.isChunkClaimed(chunkCord)) {
                UUID ownerUUID = cache.getChunkOwnerUUID(chunkCord);
                if (!cache.isChunkOwner(chunkCord, uuid) && !cache.isChunkTrusted(chunkCord, uuid) && !cache.isGlobalTrusted(ownerUUID, uuid)) toggleChunkFly(player, ChunkWarnings.FLY_OUTSIDE);
            } else toggleChunkFly(player, ChunkWarnings.FLY_OUTSIDE);
        }
    }

    //portal claim check
    @EventHandler
    public void onPortalCreate(PlayerPortalEvent e) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Data data = plugin.getData();
        Cache cache = plugin.getCache();

        UUID uuid = e.getPlayer().getUniqueId();
        Chunk chunk = e.getTo().getChunk();
        String chunkCord = plugin.getPU().formatChunkLocation(chunk);

        if (!e.getTo().getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            if (cache.isChunkClaimed(chunkCord)) {
                if (!data.hasAdminBypass(uuid)) {
                    UUID ownerUUID = cache.getChunkOwnerUUID(chunkCord);
                    if (!cache.isChunkOwner(chunkCord, uuid)) {
                        if (!cache.isChunkTrusted(chunkCord, uuid) && !cache.isGlobalTrusted(ownerUUID, uuid)) e.setCancelled(true);
                    }
                }
            } else if (cache.isAdminChunk(chunkCord)) e.setCancelled(true);
        }
    }

    private void toggleChunkFly(Player player, ChunkWarnings type) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        Cache cache = plugin.getCache();
        UUID uuid = player.getUniqueId();
        player.setAllowFlight(false);
        player.setFlying(false);
        cache.setChunkFlying(uuid, false);
        player.addPotionEffect(PotionEffectType.SLOW_FALLING.createEffect(20*15, 1));
        if (type.equals(ChunkWarnings.FLY_OUTSIDE)) player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_FLY_OUTSIDE_OF_CLAIM.getComponent(null)));
        else if (type.equals(ChunkWarnings.FLY_PVP)) player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_COMMAND_FLY_PVP.getComponent(null)));
    }
}
