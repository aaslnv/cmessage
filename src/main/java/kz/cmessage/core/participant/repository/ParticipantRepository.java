package kz.cmessage.core.participant.repository;

import kz.cmessage.core.enumiration.ParticipantType;
import kz.cmessage.core.participant.model.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findAllByLeftIsFalseAndPrimaryKeyConversationId(Long conversationId);

    boolean existsByLeftIsFalseAndPrimaryKeyConversationIdAndPrimaryKeyUserId(Long conversationId, Long userId);

    boolean existsByLeftIsFalseAndPrimaryKeyConversationIdAndPrimaryKeyUserIdAndType(Long conversationId, Long userId, ParticipantType type);

    Optional<Participant> findByPrimaryKeyConversationIdAndPrimaryKeyUserId(Long conversationId, Long userId);

    @Query("UPDATE Participant p SET p.type = kz.cmessage.core.enumiration.ParticipantType.ADMIN " +
            "WHERE p.primaryKey.conversation.id = :conversationId " +
            "AND p.primaryKey.user.id = :userId " +
            "AND p.isLeft = false")
    void setAdminByConversationIdAndUserId(@Param(value = "conversationId") Long conversationId,
                                           @Param(value = "userId") Long userId);

    @Query("UPDATE Participant p SET p.isLeft = true " +
            "WHERE p.primaryKey.conversation.id = :conversationId " +
            "AND p.primaryKey.user.id = :userId")
    void leaveByConversationIdAndUserId(@Param(value = "conversationId") Long conversationId,
                                        @Param(value = "userId") Long userId);
}
