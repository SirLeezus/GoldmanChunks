package lee.code.mychunks.files.defaults;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

@AllArgsConstructor
public enum Config {

    AMOUNT_FORMAT("general-settings.amount-format", "#,###"),
    INTERFACE_FILLER_GLASS_ITEM("interface-items.filler-glass-item", "GRAY_STAINED_GLASS_PANE"),
    INTERFACE_BACK_ITEM("interface-items.back-item", "BARRIER"),
    INTERFACE_CLOSE_ITEM("interface-items.close-item", "BARRIER"),
    INTERFACE_PERMISSION_ITEM_TRUE("interface-items.permission-item-true", "LIME_STAINED_GLASS_PANE"),
    INTERFACE_PERMISSION_ITEM_FALSE("interface-items.permission-item-false", "RED_STAINED_GLASS_PANE"),
    INTERFACE_NEXT_PAGE_ITEM("interface-items.next-page-item", "PAPER"),
    INTERFACE_PREVIOUS_PAGE_ITEM("interface-items.previous-page-item", "PAPER"),
    ;

    @Getter private final String path;
    @Getter private final String def;
    @Setter private static FileConfiguration file;

    public String getDefault() {
        return def;
    }

    public String getConfigValue(final String[] args) {
        String fileValue = file.getString(this.path, this.def);
        if (fileValue == null) fileValue = "";

        String value = ChatColor.translateAlternateColorCodes('&', fileValue);

        if (args == null) return value;
        else if (args.length == 0) return value;

        for (int i = 0; i < args.length; i++) value = value.replace("{" + i + "}", args[i]);

        return value;
    }
}

