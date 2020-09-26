package kz.cmessage.core.conversation.model;

import kz.cmessage.core.enumiration.ConversationType;
import kz.cmessage.core.user.model.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "conversation")
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false, updatable = false)
    private User creator;

    @Column(name = "photo_url")
    private String photoUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ConversationType type;

    @Column(length = 64)
    private String title;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "last_message_date")
    private LocalDateTime lastMessageDate;
}
