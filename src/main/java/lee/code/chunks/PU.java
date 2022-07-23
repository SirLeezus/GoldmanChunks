package lee.code.chunks;

import lee.code.chunks.lists.Lang;
import lee.code.chunks.lists.RenderTypes;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public class PU {

    public String serializeChunkLocation(Chunk chunk) {
        return chunk.getWorld().getName() + "," + chunk.getX() + "," + chunk.getZ();
    }

    public Location parseChunkLocation(String chunk) {
        String[] split = chunk.split(",", 3);
        return new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]) * 16, 150, Double.parseDouble(split[2]) * 16, (float) 180.0, (float) 0.0);
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

        long maxX = minX + 16;
        long maxZ = minZ + 16;
        long maxY = minY + 7;

        for (long y = minY; y < maxY; y++) {
            for (long x = minX; x <= maxX; x++) {
                for (long z = minZ; z <= maxZ; z++) {
                    player.spawnParticle(particle, minX, y, z, 0);
                    player.spawnParticle(particle, x, y, minZ, 0);
                    player.spawnParticle(particle, maxX, y, z, 0);
                    player.spawnParticle(particle, x, y, maxZ, 0);
                }
            }
        }
    }

    public void teleportPlayerToChunk(Player player, Location location) {
        World world = location.getWorld();

        int y = 200;
        int x = location.getBlockX() + 8;
        int z = location.getBlockZ() + 8;
        float yaw = location.getYaw();
        float pitch = location.getPitch();

        Material[] blackList = new Material[] { Material.AIR, Material.CAVE_AIR, Material.VOID_AIR, Material.LAVA, Material.BEDROCK };
        Material[] whiteList = new Material[] { Material.AIR, Material.CAVE_AIR, Material.VOID_AIR};

        if (world.getWorldBorder().isInside(location)) {
            world.getChunkAtAsync(location, false).thenAccept(result -> {
                for (int i = y; i > 50; i--) {
                    Location loc = new Location(world, x, i, z, yaw, pitch);
                    if (Arrays.asList(whiteList).contains(loc.getBlock().getType())) {
                        Location ground = loc.subtract(0, 1, 0);
                        Block groundBlock = ground.getBlock();
                        if (!Arrays.asList(blackList).contains(groundBlock.getType())) {
                            player.teleportAsync(loc.add(0, 1, 0)).thenAccept(result2 -> player.sendActionBar(Lang.TELEPORT.getComponent(null)));
                            return;
                        }
                    }
                }
                player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_TELEPORT_UNSAFE.getComponent(null)));
            });
        } else player.sendMessage(Lang.PREFIX.getComponent(null).append(Lang.ERROR_TELEPORT_UNSAFE.getComponent(null)));
    }

    public List<String> getChunksAroundPlayer(Location location) {
        int[] offset = {-1, 0, 1};

        World world = location.getWorld();
        int baseX = location.getChunk().getX();
        int baseZ = location.getChunk().getZ();

        List<String> chunksAroundPlayer = new ArrayList<>();
        for (int x : offset) {
            for (int z : offset) {
                Chunk chunk = world.getChunkAt(baseX + x, baseZ + z);
                chunksAroundPlayer.add(serializeChunkLocation(chunk));
            }
        }
        return chunksAroundPlayer;
    }
}
