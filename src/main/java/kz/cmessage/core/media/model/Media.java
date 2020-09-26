package kz.cmessage.core.media.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "media")
public class Media {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "media_url", nullable = false, updatable = false)
    private String mediaUrl;

    @Column(name = "file_name", nullable = false, updatable = false)
    private String fileName;

    @Column(name = "mime_type", nullable = false, updatable = false)
    private String mimeType;
}
