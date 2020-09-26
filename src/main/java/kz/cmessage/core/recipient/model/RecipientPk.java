package kz.cmessage.core.recipient.model;

import kz.cmessage.core.user.model.User;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class RecipientPk implements Serializable {

    @Column(name = "message_id", nullable = false, updatable = false)
    private Long messageId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;
}
