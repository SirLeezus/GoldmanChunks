package lee.code.chunks;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lee.code.chunks.database.Cache;
import lee.code.chunks.lists.chunksettings.ChunkAdminSettings;
import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.RenderTypes;
import lee.code.chunks.lists.Settings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PU {

    public String format(String format) {
        return ChatColor.translateAlternateColorCodes('&', format);
    }

    public Component formatC(String message) {
        LegacyComponentSerializer serializer = LegacyComponentSerializer.legacyAmpersand();
        return Component.empty().decoration(TextDecoration.ITALIC, false).append(serializer.deserialize(message));
    }

    public String unFormatC(Component message) {
        PlainTextComponentSerializer serializer = PlainTextComponentSerializer.plainText();
        return serializer.serialize(message);
    }

    public String formatChunkLocation(Chunk chunk) {
        return chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
    }

    public Location unFormatChunkLocation(String chunk) {
        String[] split = chunk.split(",", 3);
        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]) * 16, 100, Double.parseDouble(split[2]) * 16, (float) 180.0, (float) 0.0);
    }

    public String formatAmount(int value) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(value);
    }

    public String formatAmount(double value) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(value);
    }

    public List<String> getAdminChunkSettings() {
        return EnumSet.allOf(ChunkAdminSettings.class).stream().map(ChunkAdminSettings::name).collect(Collectors.toList());
    }

    public List<String> getOnlinePlayers() {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) players.add(player.getName());
        return players;
    }

    public void renderChunkBorder(Player player, Chunk chunk, RenderTypes type) {
        Particle particle = switch (type) {
            case UNCLAIM -> Particle.FLAME;
            case INFO -> Particle.END_ROD;
            default -> Particle.VILLAGER_HAPPY;
        };

        long minX = chunk.getX() * 16L;
        long minZ = chunk.getZ() * 16L;
        long minY = player.getLocation().getBlockY();

        long maxX =  minX + 17;
        long maxZ = minZ + 17;
        long maxY = minY + 7;

        for (long y = minY - 2; y < maxY; y++) {
            for (long x = minX; x < maxX; x++) {
                for (long z = minZ; z < maxZ; z++) {
                    player.spawnParticle(particle, minX, y, z, 0);
                    player.spawnParticle(particle, x, y, minZ, 0);
                    player.spawnParticle(particle, maxX - 1, y, z, 0);
                    player.spawnParticle(particle, x, y, maxZ - 1, 0);
                }
            }
        }
    }

    public void teleportPlayerToChunk(Player player, Location location) {
        World world = location.getWorld();

        int y = location.getBlockY();
        int x = location.getBlockX() + 8;
        int z = location.getBlockZ() + 8;
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        if (world.getWorldBorder().isInside(location)) {
            world.loadChunk(location.getChunk());
            for (int i = y; i > 0; i--) {
                Location loc = new Location(player.getWorld(), x, i, z, yaw, pitch);
                if (loc.getBlock().getType() == Material.AIR) {
                    Location ground = new Location(loc.getWorld(), loc.getX(), loc.getY() - 1, loc.getZ());
                    Block block = ground.getBlock();
                    if (block.getType() != Material.AIR && block.getType() != Material.LAVA) {
                        double bX = block.getBoundingBox().getCenter().getX();
                        double bY = block.getBoundingBox().getCenter().getY() + 0.5;
                        double bZ = block.getBoundingBox().getCenter().getZ();
                        Location teleportLocation = new Location(block.getWorld(), bX, bY, bZ, yaw, pitch);
                        player.teleportAsync(teleportLocation);
                        player.sendActionBar(Lang.TELEPORT.getComponent(null));
                        return;
                    }
                }
            }
        }
        player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_TELEPORT_UNSAFE.getComponent(null)));
    }

    public List<String> getChunksAroundPlayer(Player player) {
        int[] offset = {-1, 0, 1};

        World world = player.getWorld();
        int baseX = player.getLocation().getChunk().getX();
        int baseZ = player.getLocation().getChunk().getZ();

        List<String> chunksAroundPlayer = new ArrayList<>();
        for (int x : offset) {
            for (int z : offset) {
                Chunk chunk = world.getChunkAt(baseX + x, baseZ + z);
                chunksAroundPlayer.add(formatChunkLocation(chunk));
            }
        }
        return chunksAroundPlayer;
    }

    public void addPlayerClickDelay(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        plugin.getData().addPlayerClickDelay(uuid);
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.runTaskLater(plugin, () -> plugin.getData().removePlayerClickDelay(uuid), Settings.CLICK_DELAY.getValue());
    }

    public void accruedClaimTimer() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        Cache cache = plugin.getCache();

        scheduler.scheduleSyncRepeatingTask(plugin, () -> {
            int maxAccruedClaims = Settings.ACCRUED_CLAIMS_MAX.getValue();
            int baseTimeRequired = Settings.ACCRUED_CLAIMS_BASE_TIME_REQUIRED.getValue();
            int claimAmountGiven = Settings.ACCRUED_CLAIMS_AMOUNT_GIVEN.getValue();

            if (!Bukkit.getOnlinePlayers().isEmpty()) {

                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    int time = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
                    int accruedClaims = time / baseTimeRequired * claimAmountGiven;
                    if (accruedClaims > maxAccruedClaims) accruedClaims = maxAccruedClaims;

                    cache.setAccruedClaimsAmount(uuid, accruedClaims);
                }
            }
        }, 0L, 20L * 300);
    }

    public String formatSeconds(long time) {
        long days = TimeUnit.SECONDS.toDays(time);
        long hours = (TimeUnit.SECONDS.toHours(time) - TimeUnit.DAYS.toHours(days));
        long minutes = (TimeUnit.SECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(hours) - TimeUnit.DAYS.toMinutes(days));
        long seconds = (TimeUnit.SECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.DAYS.toSeconds(days));

        if (days != 0) return "&e" + days + "&6d&e, " + hours + "&6h&e, " + minutes + "&6m&e, " + seconds + "&6s";
        else if (hours != 0) return "&e" + hours + "&6h&e, " + minutes + "&6m&e, " + seconds + "&6s";
        else if (minutes != 0) return "&e" + minutes + "&6m&e, " + seconds + "&6s";
        else return "&e" + seconds + "&6s";
    }

    public void applyHeadSkin(ItemStack head, String base64) {

        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64));

        if (skullMeta != null) {
            try {
                Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
                mtd.setAccessible(true);
                mtd.invoke(skullMeta, profile);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        head.setItemMeta(skullMeta);
    }
}
