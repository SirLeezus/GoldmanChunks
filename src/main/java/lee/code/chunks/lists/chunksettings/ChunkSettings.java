package lee.code.chunks.lists.chunksettings;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ChunkSettings {
    EXPLOSIONS("chunkExplode", "explosions"),
    PVP("chunkPvP", "pvp"),
    MONSTERS("chunkMonsters", "monster_spawning"),
    ;

    @Getter private final String redisKey;
    @Getter private final String sqliteKey;
}
