package kz.cmessage.core.participant.repository;

import kz.cmessage.core.enumiration.ParticipantType;
import kz.cmessage.core.participant.model.Participant;
import kz.cmessage.core.participant.model.ParticipantPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, ParticipantPk> {

    List<Participant> findAllByLeftIsFalseAndPrimaryKeyConversationId(Long conversationId);

    boolean existsByLeftIsFalseAndPrimaryKeyConversationIdAndPrimaryKeyUserId(Long conversationId, Long userId);

    boolean existsByPrimaryKeyConversationIdAndPrimaryKeyUserId(Long conversationId, Long userId);

    boolean existsByLeftIsFalseAndPrimaryKeyConversationIdAndPrimaryKeyUserIdAndType(Long conversationId, Long userId, ParticipantType type);

    Optional<Participant> findByPrimaryKeyConversationIdAndPrimaryKeyUserId(Long conversationId, Long userId);

    @Query("UPDATE Participant p SET p.type = kz.cmessage.core.enumiration.ParticipantType.ADMIN " +
            "WHERE p.primaryKey.conversation.id = :conversationId " +
            "AND p.primaryKey.user.id = :userId " +
            "AND p.isLeft = false")
    void setTypeIsAdminByConversationIdAndUserId(@Param(value = "conversationId") Long conversationId,
                                                 @Param(value = "userId") Long userId);

    @Query("UPDATE Participant p SET p.type = kz.cmessage.core.enumiration.ParticipantType.COMMON " +
            "WHERE p.primaryKey.conversation.id = :conversationId " +
            "AND p.primaryKey.user.id = :userId " +
            "AND p.isLeft = false")
    void setTypeIsCommonByConversationIdAndUserId(@Param(value = "conversationId") Long conversationId,
                                                  @Param(value = "userId") Long userId);

    @Query("UPDATE Participant p SET p.isLeft = true " +
            "WHERE p.primaryKey.conversation.id = :conversationId " +
            "AND p.primaryKey.user.id = :userId")
    void leaveByConversationIdAndUserId(@Param(value = "conversationId") Long conversationId,
                                        @Param(value = "userId") Long userId);

    @Query("UPDATE Participant p SET p.isLeft = false, p.type = kz.cmessage.core.enumiration.ParticipantType.COMMON " +
            "WHERE p.primaryKey.conversation.id = :conversationId " +
            "AND p.primaryKey.user.id IN :userIds")
    void setLeftIsFalseByConversationIdAndUserIds(@Param(value = "conversationId") Long conversationId,
                                                  @Param(value = "userIds") List<Long> userIds);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Participant p " +
            "WHERE p.primaryKey.conversation.id = :conversationId " +
            "AND p.primaryKey.user.id <> :userId " +
            "AND p.isLeft = false " +
            "AND p.type = kz.cmessage.core.enumiration.ParticipantType.ADMIN")
    boolean existsOtherAdminsByConversationIdAndUserId(@Param(value = "conversationId") Long conversationId,
                                                       @Param(value = "userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Participant p " +
            "WHERE p.primaryKey.conversation.id = :conversationId " +
            "AND p.primaryKey.user.id <> :userId " +
            "AND p.isLeft = false")
    boolean existsOtherParticipantsByConversationIdAndUserId(@Param(value = "conversationId") Long conversationId,
                                                       @Param(value = "userId") Long userId);

    Participant findFirstByLeftIsFalseAndPrimaryKeyConversationIdAndPrimaryKeyUserIdAndTypeOrderByCreatedDateDesc(Long conversationId, Long userId, ParticipantType type);
}
