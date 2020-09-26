package kz.cmessage.core.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DefaultResponseMessage {

    ILLEGAL_ACCESS_ATTEMPT_DENIED("Illegal access attempt denied"),
    INVALID_PAYLOAD("Invalid payload");

    private String value;
}
