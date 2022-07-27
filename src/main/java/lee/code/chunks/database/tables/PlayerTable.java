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
@DatabaseTable(tableName = "player")
public class PlayerTable {

    @DatabaseField(id = true, canBeNull = false)
    private UUID player;

    @DatabaseField(columnName = "claimed", canBeNull = false)
    private int claimed;

    @DatabaseField(columnName = "bonus_claims", canBeNull = false)
    private int bonusClaims;

    @DatabaseField(columnName = "global_trusted", canBeNull = false)
    private String globalTrusted;

    @DatabaseField(columnName = "global_build", canBeNull = false)
    private boolean globalBuild;

    @DatabaseField(columnName = "global_break", canBeNull = false)
    private boolean globalBreak;

    @DatabaseField(columnName = "global_interact", canBeNull = false)
    private boolean globalInteract;

    @DatabaseField(columnName = "global_pve", canBeNull = false)
    private boolean globalPVE;

    @DatabaseField(columnName = "chunk_flying", canBeNull = false)
    private boolean chunkFlying;

    @DatabaseField(columnName = "blocked_players", canBeNull = false)
    private String blockedPlayers;

    public PlayerTable(UUID player) {
        this.player = player;
        this.claimed = 0;
        this.bonusClaims = 0;
        this.globalTrusted = "0";
        this.globalBuild = true;
        this.globalBreak = true;
        this.globalInteract = true;
        this.globalPVE = true;
        this.chunkFlying = false;
        this.blockedPlayers = "0";
    }
}