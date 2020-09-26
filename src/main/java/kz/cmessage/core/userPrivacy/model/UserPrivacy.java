package kz.cmessage.core.userPrivacy.model;

import lombok.Data;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "user_privacy")
public class UserPrivacy {

    @EmbeddedId
    private UserPrivacyPk primaryKey;
}
