package lee.code.mychunks.softdependency;

import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.entity.Player;

public class WorldGuardAPI {

    public boolean canBuild(Player player) {
        LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        Location loc = new Location(wgPlayer.getWorld(), wgPlayer.getLocation().getX(), wgPlayer.getLocation().getY(), wgPlayer.getLocation().getZ());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        return query.testBuild(loc, wgPlayer);
    }
}
