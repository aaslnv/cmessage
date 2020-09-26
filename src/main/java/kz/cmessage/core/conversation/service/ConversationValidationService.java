package kz.cmessage.core.conversation.service;

import kz.cmessage.core.conversation.dto.ConversationDto;
import kz.cmessage.core.conversation.model.Conversation;
import kz.cmessage.core.conversation.repository.ConversationRepository;
import kz.cmessage.core.enumiration.ConversationType;
import kz.cmessage.core.exception.ValidationException;
import kz.cmessage.core.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class ConversationValidationService {

    public static final Integer GROUP_MIN_PARTICIPANTS = 3;
    public static final Integer PRIVATE_MAX_PARTICIPANTS = 2;

    private ConversationService conversationService;
    private ConversationRepository conversationRepository;


    public ConversationValidationService(ConversationService conversationService, ConversationRepository conversationRepository) {
        this.conversationService = conversationService;
        this.conversationRepository = conversationRepository;
    }

    public void validateUserIsNotLeftParticipant(User user, Conversation conversation) throws ValidationException {
        long filteredCount = conversation.getParticipants().stream()
                .filter(participant -> participant.getPrimaryKey().getUser().getId().equals(user.getId()) &&
                        !participant.isLeft())
                .count();
        if (filteredCount == 0L) {
            throw new ValidationException("Current user is not a participant ot this conversation");
        }
    }

    public void validateUserIsCreatorByConversationIdAndUserId(Long conversationId, Long userId) throws ValidationException {
        if (!conversationRepository.existsByIdAndCreatorId(conversationId, userId)) {
            throw new ValidationException("Creator user id and session user id is not match");
        }
    }

    public void validateUserIsCreatorOrAdmin(User user, ConversationDto dto) throws ValidationException {
        validateUserIsCreator(user, dto);

    }

    public void validateConversationTypeByParticipantsCount(ConversationDto dto) throws ValidationException {
        if (dto.getType() == ConversationType.GROUP && dto.getParticipantUserIds().size() < GROUP_MIN_PARTICIPANTS) {
            throw new ValidationException(format("Group conversation should have %s or more participants", GROUP_MIN_PARTICIPANTS));
        } else if (dto.getType() == ConversationType.PRIVATE && dto.getParticipantUserIds().size() > PRIVATE_MAX_PARTICIPANTS) {
            throw new ValidationException(format("Private conversation should have no more than %s participants", PRIVATE_MAX_PARTICIPANTS));
        }
    }

    public void validateDtoByPersistentConversationId(@NonNull ConversationDto dto, @NonNull Long conversationId) throws ValidationException {
        if (!conversationId.equals(dto.getId())) {
            throw new ValidationException(format("ConversationDto [id = %s] and Conversation [id = %s] is not match", dto.getId(), conversationId));
        }
    }

}
