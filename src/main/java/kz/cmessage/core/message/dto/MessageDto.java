package kz.cmessage.core.message.dto;

import kz.cmessage.core.enumiration.MessageStatus;
import kz.cmessage.core.enumiration.MessageType;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class MessageDto {

    private Long id;

    @NotNull
    private Long conversationId;

    @NotNull
    private Long senderId;

    private MessageType type;

    private MessageStatus status;

    private String text;

    private Long mediaId;

    private LocalDateTime sentDate;
}
