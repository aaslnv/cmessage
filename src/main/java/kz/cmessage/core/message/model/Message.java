package kz.cmessage.core.message.model;

import kz.cmessage.core.conversation.model.Conversation;
import kz.cmessage.core.enumiration.MessageStatus;
import kz.cmessage.core.enumiration.MessageType;
import kz.cmessage.core.media.model.Media;
import kz.cmessage.core.recipient.model.Recipient;
import kz.cmessage.core.user.model.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false, updatable = false)
    private Conversation conversation;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false, updatable = false)
    private User sender;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, updatable = false, length = 16)
    private MessageType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private MessageStatus status;

    @Column(length = 1000)
    private String text;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "media_id", updatable = false)
    private Media media;

    @Column(name = "sent_date", nullable = false, updatable = false)
    private LocalDateTime sentDate;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "message_id")
    private List<Recipient> recipients;
}
