package lee.code.chunks.lists;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Values {

    CLICK_DELAY(5),
    ACCRUED_CLAIMS_MAX(250),
    ACCRUED_CLAIMS_BASE_TIME_REQUIRED(3600),
    ACCRUED_CLAIMS_AMOUNT_GIVEN(5),
    ;

    @Getter private final int value;
}