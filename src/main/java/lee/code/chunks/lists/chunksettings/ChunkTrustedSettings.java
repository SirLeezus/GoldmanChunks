package lee.code.chunks.lists.chunksettings;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ChunkTrustedSettings {
    BUILD("chunkTrustedBuild", "build"),
    BREAK("chunkTrustedBreak", "break"),
    INTERACT("chunkTrustedInteract", "interact"),
    PVE("chunkTrustedPvE", "pve"),
    ;

    @Getter private final String redisKey;
    @Getter private final String sqliteKey;
}
