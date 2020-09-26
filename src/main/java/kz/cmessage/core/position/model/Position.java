package kz.cmessage.core.position.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "position")
public class Position {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_translation_code", nullable = false)
    private String nameTranslationCode;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
}
