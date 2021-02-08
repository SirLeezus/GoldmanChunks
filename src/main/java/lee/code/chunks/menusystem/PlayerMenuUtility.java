package lee.code.chunks.menusystem;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class PlayerMenuUtility {

    private final UUID owner;
    @Getter @Setter private int chunkListPage;
    public Player getOwner() {
        return Bukkit.getPlayer(owner);
    }
}