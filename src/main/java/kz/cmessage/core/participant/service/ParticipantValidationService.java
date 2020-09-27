package kz.cmessage.core.participant.service;

import kz.cmessage.core.conversation.dto.ConversationDto;
import kz.cmessage.core.conversation.model.Conversation;
import kz.cmessage.core.enumiration.ParticipantType;
import kz.cmessage.core.participant.repository.ParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ParticipantValidationService {

    private ParticipantRepository participantRepository;

    public ParticipantValidationService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public boolean isUserNotLeftParticipantByConversationIdAndUserId(Long conversationId, Long userId) {
        return participantRepository.existsByLeftIsFalseAndPrimaryKeyConversationIdAndPrimaryKeyUserId(conversationId, userId);
    }

    public boolean isUserParticipantByConversationIdAndUserId(Long conversationId, Long userId) {
        return participantRepository.existsByPrimaryKeyConversationIdAndPrimaryKeyUserId(conversationId, userId);
    }

    public boolean isUserAdminByConversationIdAndUserId(Long conversationId, Long userId) {
        return participantRepository.existsByLeftIsFalseAndPrimaryKeyConversationIdAndPrimaryKeyUserIdAndType(
                conversationId, userId, ParticipantType.ADMIN);
    }
}
