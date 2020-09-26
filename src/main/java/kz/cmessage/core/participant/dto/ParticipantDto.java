package kz.cmessage.core.participant.dto;

import kz.cmessage.core.enumiration.ParticipantType;
import kz.cmessage.core.user.dto.UserDto;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
public class ParticipantDto {

    private Long conversationId;
    @Valid
    @NotNull
    private UserDto user;
    private ParticipantType type;
    private boolean isLeft;
}
