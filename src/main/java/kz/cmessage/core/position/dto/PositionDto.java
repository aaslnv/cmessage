package kz.cmessage.core.position.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class PositionDto {

    @NotBlank
    private String name;

    private boolean isDeleted;
}
