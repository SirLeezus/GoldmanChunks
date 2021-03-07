package lee.code.chunks.lists;

import lee.code.chunks.GoldmanChunks;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public enum MenuItems {
    FILLER_GLASS(Material.GRAY_STAINED_GLASS_PANE, "&r", null, null),
    BACK_MENU(Material.BARRIER, "&6&l<- Back", null, null),
    CLOSE_MENU(Material.BARRIER, "&c&lClose", null, null),
    NEXT_PAGE(Material.PAPER, "&eNext Page >", null, null),
    PREVIOUS_PAGE(Material.PAPER, "&e< Previous Page", null, null),
    TRUSTED(Material.PLAYER_HEAD, "&2&lTrusted Chunk Settings", "&7Toggle your trusted player settings\n&7on the chunk you are standing on.", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjE5ZTM2YTg3YmFmMGFjNzYzMTQzNTJmNTlhN2Y2M2JkYjNmNGM4NmJkOWJiYTY5Mjc3NzJjMDFkNGQxIn19fQ=="),
    GLOBAL_TRUSTED(Material.PLAYER_HEAD, "&2&lTrusted Global Settings", "&7Toggle your trusted player global\n&7settings on all your claimed chunks.", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzY5MTk2YjMzMGM2Yjg5NjJmMjNhZDU2MjdmYjZlY2NlNDcyZWFmNWM5ZDQ0Zjc5MWY2NzA5YzdkMGY0ZGVjZSJ9fX0="),
    GENERAL(Material.PLAYER_HEAD, "&e&lGeneral Chunk Settings", "&7Toggle settings for the chunk you\n&7are standing on.", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmZhNzY0YjNjMWQ0NjJmODEyNDQ3OGZmNTQzYzc2MzNmYTE5YmFmOTkxM2VlMjI4NTEzZTgxYTM2MzNkIn19fQ=="),
    PERM_TRUE(Material.LIME_STAINED_GLASS_PANE, "", null, null),
    PERM_FALSE(Material.RED_STAINED_GLASS_PANE, "", null, null),
    ;

    @Getter private final Material type;
    @Getter private final String name;
    @Getter private final String lore;
    @Getter private final String skin;

    public ItemStack getItem() {
        GoldmanChunks plugin = GoldmanChunks.getPlugin();
        ItemStack item = new ItemStack(type);
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            if (name != null) itemMeta.displayName(Component.text(ChatColor.translateAlternateColorCodes('&', name)));
            if (lore != null) {
                String[] split = StringUtils.split(lore, "\n");
                List<Component> lines = new ArrayList<>();
                for (String line : split) lines.add(Component.text(ChatColor.translateAlternateColorCodes('&', line)));
                itemMeta.lore(lines);
            }
            item.setItemMeta(itemMeta);
            if (skin != null) plugin.getPU().applyHeadSkin(item, skin);
        }
        return item;
    }
}