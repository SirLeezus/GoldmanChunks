package lee.code.chunks;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lee.code.chunks.database.SQLite;
import lee.code.chunks.lists.Values;
import lombok.SneakyThrows;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitScheduler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class PU {

    //color formatting string
    public String format(String format) {
        return ChatColor.translateAlternateColorCodes('&', format);
    }

    //format chunk
    public String formatChunk(Chunk chunk) {
        return chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
    }

    //format an amount
    public String formatAmount(int value) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(value);
    }

    //get online players
    public List<String> getOnlinePlayers() {
        List<String> players = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            players.add(player.getName());
        }
        return players;
    }

    public void renderChunkBorder(Player player, Chunk chunk, String type) {

        Particle particle = Particle.VILLAGER_HAPPY;
        if (type.equals("unclaim")) {
            particle = Particle.FLAME;
        } else if (type.equals("info")) {
            particle = Particle.CLOUD;
        }

        int minX = chunk.getX() * 16;
        int minZ = chunk.getZ() * 16;
        int minY = player.getLocation().getBlockY();

        for (int y = minY - 2; y < minY + 7; y++) {
            for (int x = minX; x < minX + 17; x++) {
                for (int z = minZ; z < minZ + 17; z++) {
                    player.spawnParticle(particle, minX, y + 1, z, 0);
                    player.spawnParticle(particle, x, y + 1, minZ, 0);
                    player.spawnParticle(particle, minX + 16, y + 1, z, 0);
                    player.spawnParticle(particle, x, y + 1, minZ + 16, 0);
                }
            }
        }
    }

    public Collection<Chunk> getChunksAroundPlayer(Player player) {
        int[] offset = {-1, 0, 1};

        World world = player.getWorld();
        int baseX = player.getLocation().getChunk().getX();
        int baseZ = player.getLocation().getChunk().getZ();

        Collection<Chunk> chunksAroundPlayer = new HashSet<>();
        for (int x : offset) {
            for (int z : offset) {
                Chunk chunk = world.getChunkAt(baseX + x, baseZ + z);
                chunksAroundPlayer.add(chunk);
            }
        }
        return chunksAroundPlayer;
    }

    public void addPlayerClickDelay(UUID uuid) {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        plugin.getData().addPlayerClickDelay(uuid);
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.runTaskLater(plugin, () ->
                plugin.getData().removePlayerClickDelay(uuid), Values.CLICK_DELAY.getValue());
    }

    public void accruedClaimTimer() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        SQLite SQL = plugin.getSqLite();

        scheduler.scheduleSyncRepeatingTask(plugin, () -> {
            int maxAccruedClaims = Values.ACCRUED_CLAIMS_MAX.getValue();
            int baseTimeRequired = Values.ACCRUED_CLAIMS_BASE_TIME_REQUIRED.getValue();
            int claimAmountGiven = Values.ACCRUED_CLAIMS_AMOUNT_GIVEN.getValue();

            if (!Bukkit.getOnlinePlayers().isEmpty()) {

                for (Player player : Bukkit.getOnlinePlayers()) {
                    UUID uuid = player.getUniqueId();
                    int time = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20;
                    int accruedClaims = time / baseTimeRequired * claimAmountGiven;
                    if (accruedClaims > maxAccruedClaims) accruedClaims = maxAccruedClaims;

                    SQL.setAccruedClaims(uuid, accruedClaims);
                }
            }
        }, 0L, 20L * 300);
    }

    public String formatTime(int time) {
        int days = (int) TimeUnit.SECONDS.toDays(time);
        int hours = (int) (TimeUnit.SECONDS.toHours(time) - TimeUnit.DAYS.toHours(days));
        int minutes = (int) (TimeUnit.SECONDS.toMinutes(time) - TimeUnit.HOURS.toMinutes(hours) - TimeUnit.DAYS.toMinutes(days));
        int seconds = (int) (TimeUnit.SECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(minutes) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.DAYS.toSeconds(days));

        if (days != 0) return days + " day, " + hours + " hours, " + minutes + " min, " + seconds + " sec";
        else if (hours != 0) return hours + " hour, " + minutes + " min, " + seconds + " sec";
        else if (minutes != 0) return minutes + " min, " + seconds + " sec";
        else return seconds + " sec";
    }

    @SneakyThrows
    public void applyHeadSkin(ItemStack head, String base64) {

        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64));

        if (skullMeta != null) {
            Method mtd = skullMeta.getClass().getDeclaredMethod("setProfile", GameProfile.class);
            mtd.setAccessible(true);
            mtd.invoke(skullMeta, profile);
        }
        head.setItemMeta(skullMeta);
    }
}
