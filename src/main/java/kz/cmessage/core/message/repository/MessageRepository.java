package kz.cmessage.core.message.repository;

import kz.cmessage.core.message.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT DISTINCT m FROM Message m " +
            "JOIN m.recipients r " +
            "WHERE m.conversation.id = :conversationId " +
            "AND r.primaryKey.user.id = :userId " +
            "AND r.isDeleted = false")
    List<Message> findAllByConversationIdAndUserId(@Param(value = "conversationId") Long conversationId,
                                                   @Param(value = "userId") Long userId);
}
