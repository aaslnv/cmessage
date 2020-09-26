package kz.cmessage.core.participant.service;

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

    public boolean validateUserIsParticipantByConversationIdAndUserId(Long conversationId, Long userId) {
        return participantRepository.existsByLeftIsFalseAndPrimaryKeyConversationIdAndPrimaryKeyUserId(conversationId, userId);
    }

    public boolean validateUserIsAdminByConversationIdAndUserId(Long conversationId, Long userId) {
        return participantRepository.existsByLeftIsFalseAndPrimaryKeyConversationIdAndPrimaryKeyUserIdAndType(
                conversationId, userId, ParticipantType.ADMIN);
    }
}
