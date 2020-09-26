package kz.cmessage.core.recipient.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "recipient")
public class Recipient {

    @EmbeddedId
    private RecipientPk primaryKey;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(name = "read_date", nullable = false)
    private LocalDateTime readDate;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "deleted_date", nullable = false)
    private LocalDateTime deletedDate;
}
