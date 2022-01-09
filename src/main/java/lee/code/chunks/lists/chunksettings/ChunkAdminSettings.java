package lee.code.chunks.lists.chunksettings;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ChunkAdminSettings {
    BUILD("adminChunkBuild", "build"),
    BREAK("adminChunkBreak", "break"),
    EXPLOSIONS("adminChunkExplode", "explosions"),
    INTERACT("adminChunkInteract", "interact"),
    PVE("adminChunkPvE", "pve"),
    MONSTERS("adminChunkMonsters", "monster_spawning"),
    ;

    @Getter private final String redisKey;
    @Getter private final String sqliteKey;
}
