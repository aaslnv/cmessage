package kz.cmessage.core.user.model;

import kz.cmessage.core.enumiration.Locale;
import kz.cmessage.core.enumiration.Role;
import kz.cmessage.core.userInformation.model.UserInformation;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false, length = 16)
    private String username;

    @Column(nullable = false, length = 1000)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Role role;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "blocked_date")
    private LocalDateTime blockedDate;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "is_online", nullable = false)
    private boolean isOnline;

    @Column(name = "last_online_date")
    private LocalDateTime lastOnlineDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Locale locale = Locale.RU;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_information_id", nullable = false, unique = true, updatable = false)
    private UserInformation userInformation;
}
