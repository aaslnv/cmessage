package kz.cmessage.core.user.dto;

import kz.cmessage.core.enumiration.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateUserByManagerRequestDto extends UpdateUserRequestDto {

    private Role role;
    private LocalDateTime blockedDate;
    private boolean isDeleted;
}
