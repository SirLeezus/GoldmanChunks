package lee.code.chunks.lists;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Settings {

    ACCRUED_CLAIMS_MAX(500),
    ACCRUED_CLAIMS_BASE_TIME_REQUIRED(3600),
    ACCRUED_CLAIMS_AMOUNT_GIVEN(1),
    CLAIMS_MAX(100000000),
    CHUNK_SELL_PRICE_MAX(1000000000),
    ;

    @Getter private final int value;
}