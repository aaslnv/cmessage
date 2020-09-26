package kz.cmessage.core.userInformation.model;

import kz.cmessage.core.department.model.Department;
import kz.cmessage.core.position.model.Position;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "user_information")
public class UserInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 40)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 40)
    private String lastName;

    @Column(name = "phone", nullable = false, unique = true, length = 16)
    private String phone;

    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @Column(name = "status", length = 64)
    private String status;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @ManyToOne
    @JoinColumn(name = "position_id", nullable = false)
    private Position position;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;
}
