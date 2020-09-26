package kz.cmessage.core.session.model;

import kz.cmessage.core.enumiration.SessionStatus;
import kz.cmessage.core.user.model.User;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "session")
public class Session {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo;

    @Column(name = "expiry", nullable = false)
    private LocalDateTime expiry;

    @Column(name = "logout", nullable = false)
    private LocalDateTime logout;

    @Column(name = "is_blocked")
    private boolean isBlocked;

    @Column(nullable = false)
    private String ip;

    @Column(nullable = false)
    private String token;
}
