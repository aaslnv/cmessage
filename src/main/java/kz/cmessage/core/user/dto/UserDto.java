package kz.cmessage.core.user.dto;

import kz.cmessage.core.department.dto.DepartmentDto;
import kz.cmessage.core.enumiration.Locale;
import kz.cmessage.core.enumiration.Role;
import kz.cmessage.core.position.dto.PositionDto;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserDto {

    private Long id;
    @NotBlank
    private String username;
    @NotBlank
    private Role role;
    private LocalDateTime blockedDate;
    private boolean isDeleted;
    private boolean isOnline;
    private LocalDateTime lastOnlineDate;
    private Locale locale = Locale.RU;
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String phone;
    @NotBlank
    private String email;
    private String status;
    private String avatarUrl;
    @NotNull
    private LocalDate birthDate;
    @Valid
    @NotNull
    private PositionDto position;
    @Valid
    @NotNull
    private DepartmentDto department;
}
