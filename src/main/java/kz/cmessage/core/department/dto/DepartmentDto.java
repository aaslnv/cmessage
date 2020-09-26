package kz.cmessage.core.department.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartmentDto {

    private String name;

    private String shortName;

    private boolean isDeleted;
}