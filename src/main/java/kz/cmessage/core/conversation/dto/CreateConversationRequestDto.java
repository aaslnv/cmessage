package kz.cmessage.core.conversation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
public class CreateConversationRequestDto {

    @Valid
    @NotNull
    private ConversationDto conversation;

    @NotEmpty
    @JsonProperty("participants")
    private Set<Long> participantUserIds;
}
