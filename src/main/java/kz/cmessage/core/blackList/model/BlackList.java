package kz.cmessage.core.blackList.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "black_list")
public class BlackList {

    @EmbeddedId
    private BlackListPk primaryKey;

    @Column(name = "blocked_date", nullable = false, updatable = false)
    private LocalDateTime blockedDate;
}
