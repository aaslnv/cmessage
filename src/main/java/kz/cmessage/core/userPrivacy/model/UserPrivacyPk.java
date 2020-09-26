package kz.cmessage.core.userPrivacy.model;

import kz.cmessage.core.enumiration.PrivacyOption;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;

@Embeddable
public class UserPrivacyPk implements Serializable {

    @Column(name ="user_id", nullable = false, updatable = false)
    private Long userId;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private PrivacyOption privacy;
}
