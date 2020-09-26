package kz.cmessage.core.user.dto;

import kz.cmessage.core.enumiration.Locale;
import lombok.Data;

@Data
public class UpdateUserRequestDto {

    private String username;
    private Locale locale;
}
