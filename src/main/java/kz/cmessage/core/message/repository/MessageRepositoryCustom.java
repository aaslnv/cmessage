package kz.cmessage.core.message.repository;

import kz.cmessage.core.message.model.Message;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class MessageRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Message> findTopByConversationIdAndUserIdAndFromAndCount(@Param(value = "conversationId") Long conversationId,
                                                                         @Param(value = "userId") Long userId,
                                                                         Integer from, Integer count) {
        return entityManager.createQuery("SELECT DISTINCT m FROM Message m " +
                "JOIN m.recipients r " +
                "WHERE m.conversation.id = :conversationId " +
                "AND r.primaryKey.user.id = :userId " +
                "AND r.isDeleted = false " +
                "ORDER BY m.sentDate DESC", Message.class)
                .setFirstResult(from)
                .setMaxResults(count)
                .getResultList();

    }

}
