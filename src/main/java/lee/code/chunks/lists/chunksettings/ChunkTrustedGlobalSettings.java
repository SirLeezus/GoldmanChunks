package lee.code.chunks.lists.chunksettings;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ChunkTrustedGlobalSettings {
    BUILD("trustedGlobalBuild", "build"),
    BREAK("trustedGlobalBreak", "break"),
    INTERACT("trustedGlobalInteract", "interact"),
    PVE("trustedGlobalPvE", "pve"),
    ;

    @Getter private final String redisKey;
    @Getter private final String sqliteKey;
}
