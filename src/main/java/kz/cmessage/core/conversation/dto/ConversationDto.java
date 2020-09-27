package kz.cmessage.core.conversation.dto;

import kz.cmessage.core.enumiration.ConversationType;
import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class ConversationDto {

    private Long id;
    @NotNull
    private Long creatorId;
    private String photoUrl;
    @NotNull
    private ConversationType type;
    private String title;
    private LocalDateTime lastMessageDate;
}
