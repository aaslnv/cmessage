package kz.cmessage.core.userInformation.dto;

import kz.cmessage.core.department.dto.DepartmentDto;
import kz.cmessage.core.position.dto.PositionDto;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
public class UserInformationDto {

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
