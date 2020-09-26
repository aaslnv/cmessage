package kz.cmessage.core.blackList.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class BlackListPk implements Serializable  {

    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Column(name = "blocked_user_id", nullable = false, updatable = false)
    private Long blockedUserId;
}
