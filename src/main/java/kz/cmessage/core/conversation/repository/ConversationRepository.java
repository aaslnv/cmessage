package kz.cmessage.core.conversation.repository;

import kz.cmessage.core.conversation.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    @Query("SELECT DISTINCT c FROM Conversation c " +
            "JOIN c.participants p " +
            "WHERE p.primaryKey.user.id = :userId " +
            "AND p.isLeft = false " +
            "ORDER BY c.lastMessageDate DESC")
    List<Conversation> findAllActiveByUserId(@Param(value = "userId") Long userId);

    @Query("SELECT DISTINCT c FROM Conversation c " +
            "JOIN c.participants p " +
            "WHERE c.type = kz.cmessage.core.enumiration.ConversationType.PRIVATE " +
            "AND ((c.creator.id = :firstUserId AND p.primaryKey.user.id = :secondUserId) " +
            "OR (c.creator.id = :secondUserId AND p.primaryKey.user.id = :firstUserId))")
    Optional<Conversation> findPrivateByUserIds(@Param(value = "firstUserId") Long firstUserId,
                                                @Param(value = "secondUserId") Long secondUserId);


    boolean existsByIdAndCreatorId(Long id, Long userId);

}
