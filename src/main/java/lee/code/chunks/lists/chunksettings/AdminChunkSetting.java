package lee.code.chunks.lists.chunksettings;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum AdminChunkSetting {
    BUILD("chunk_build"),
    BREAK("chunk_break"),
    INTERACT("chunk_interact"),
    PVE("chunk_pve"),
    MONSTERS("chunk_monster_spawning"),
    EXPLOSIONS("chunk_explosions"),
    ;

    @Getter private final String sqliteKey;
}
