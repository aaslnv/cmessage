package kz.cmessage.core.conversation.service;

import kz.cmessage.core.conversation.dto.ConversationDto;
import kz.cmessage.core.conversation.model.Conversation;
import kz.cmessage.core.conversation.repository.ConversationRepository;
import kz.cmessage.core.enumiration.ConversationType;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ConversationValidationService {

    public static final Integer GROUP_MIN_PARTICIPANTS = 3;
    public static final Integer PRIVATE_MAX_PARTICIPANTS = 2;

    public boolean isRightTypeSetByConversationDtoAndParticipantsIds(ConversationDto dto, Set<Long> ids) {
        return (dto.getType() == ConversationType.GROUP && ids.size() > GROUP_MIN_PARTICIPANTS) ||
                (dto.getType() == ConversationType.PRIVATE && ids.size() < PRIVATE_MAX_PARTICIPANTS);
    }

    public boolean isSecuredFieldsChanged(Conversation dbConversation, Conversation dtoConversation) {
        return !dbConversation.getCreator().getId().equals(dtoConversation.getCreator().getId()) ||
                dbConversation.getType() != dtoConversation.getType();
    }
}
