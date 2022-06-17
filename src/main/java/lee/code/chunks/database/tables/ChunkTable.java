package lee.code.chunks.database.tables;

import lee.code.core.ormlite.field.DatabaseField;
import lee.code.core.ormlite.table.DatabaseTable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@DatabaseTable(tableName = "chunks")
public class ChunkTable {

    @DatabaseField(id = true, canBeNull = false)
    private String chunk;

    @DatabaseField(columnName = "owner", canBeNull = false)
    private UUID owner;

    @DatabaseField(columnName = "trusted", canBeNull = false)
    private String trusted;

    @DatabaseField(columnName = "trusted_build", canBeNull = false)
    private boolean trustedBuild;

    @DatabaseField(columnName = "trusted_break", canBeNull = false)
    private boolean trustedBreak;

    @DatabaseField(columnName = "trusted_interact", canBeNull = false)
    private boolean trustedInteract;

    @DatabaseField(columnName = "trusted_pve", canBeNull = false)
    private boolean trustedPVE;

    @DatabaseField(columnName = "chunk_monster_spawning", canBeNull = false)
    private boolean chunkMonsterSpawning;

    @DatabaseField(columnName = "chunk_explosions", canBeNull = false)
    private boolean chunkExplosions;

    @DatabaseField(columnName = "chunk_price", canBeNull = false)
    private long chunkPrice;

    public ChunkTable(String chunk, UUID owner) {
        this.chunk = chunk;
        this.owner = owner;
        this.trusted = "0";
        this.trustedBuild = true;
        this.trustedBreak = true;
        this.trustedInteract = true;
        this.trustedPVE = true;
        this.chunkMonsterSpawning = false;
        this.chunkExplosions = false;
        this.chunkPrice = 0;
    }
}