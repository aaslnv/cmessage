package kz.cmessage.core.conversation.dto;

import kz.cmessage.core.enumiration.ConversationType;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ConversationDto {

    private Long id;
    @NotNull
    private Long creatorId;
    private String photoUrl;
    @NotNull
    private ConversationType type;
    private String title;
}
