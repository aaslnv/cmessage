package kz.cmessage.core.blackList.model;

import kz.cmessage.core.user.model.User;
import lombok.Data;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Data
@Embeddable
public class BlackListPk implements Serializable {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "blocked_user_id", nullable = false, updatable = false)
    private User blockedUser;
}
