package kz.cmessage.core.participant.model;

import kz.cmessage.core.enumiration.ParticipantType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "participant")
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    @EmbeddedId
    private ParticipantPk primaryKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 16)
    private ParticipantType type;

    @Column(name = "is_left", nullable = false)
    private boolean isLeft;
}
