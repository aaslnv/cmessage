package kz.cmessage.core.department.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "department")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name_translation_code",nullable = false)
    private String nameTranslationCode;

    @Column(name = "short_name_translation_code", nullable = false)
    private String shortNameTranslationCode;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}