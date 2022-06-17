package lee.code.chunks.database.tables;

import lee.code.core.ormlite.field.DatabaseField;
import lee.code.core.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "admin_chunks")
public class AdminChunkTable {

    @DatabaseField(id = true, canBeNull = false)
    private String chunk;

    @DatabaseField(columnName = "chunk_build", canBeNull = false)
    private boolean chunkBuild;

    @DatabaseField(columnName = "chunk_break", canBeNull = false)
    private boolean chunkBreak;

    @DatabaseField(columnName = "chunk_interact", canBeNull = false)
    private boolean chunkInteract;

    @DatabaseField(columnName = "chunk_pve", canBeNull = false)
    private boolean chunkPVE;

    @DatabaseField(columnName = "chunk_monster_spawning", canBeNull = false)
    private boolean chunkMonsterSpawning;

    @DatabaseField(columnName = "chunk_explosions", canBeNull = false)
    private boolean chunkExplosions;

    public AdminChunkTable(String chunk) {
        this.chunk = chunk;
        this.chunkBuild = false;
        this.chunkBreak = false;
        this.chunkInteract = false;
        this.chunkPVE = false;
        this.chunkMonsterSpawning = false;
        this.chunkExplosions = false;
    }
}