package lee.code.mychunks.files.defaults;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;

@AllArgsConstructor
public enum Values {

    CLICK_DELAY("general-settings.click-delay", 5),
    ACCRUED_CLAIMS_MAX("accrued-claims.max-accrued-claims", 250),
    ACCRUED_CLAIMS_BASE_TIME_REQUIRED("accrued-claims.base-time-required", 3600),
    ACCRUED_CLAIMS_AMOUNT_GIVEN("accrued-claims.claim-amount-given", 5),
    ;

    @Getter private final String path;
    @Getter private final int def;
    @Setter private static FileConfiguration file;

    public int getDefault() {
        return this.def;
    }

    public int getConfigValue() {
        return file.getInt(this.path, this.def);
    }
}