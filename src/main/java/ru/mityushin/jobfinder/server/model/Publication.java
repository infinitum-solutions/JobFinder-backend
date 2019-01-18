package ru.mityushin.jobfinder.server.model;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Data @Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Entity
@Table(name = "PUBLICATION")
public class Publication {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "UUID", nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "AUTHOR_UUID", nullable = false)
    private UUID authorUuid;

    @Column(name = "TITLE", length = 50, nullable = false)
    private String title;

    @Column(name = "DESCRIPTION", length = 50)
    private String description;

    @Column(name = "CONTENT", length = 255, nullable = false)
    private String content;

    @Column(name = "VISIBLE", nullable = false)
    private Boolean visible;

    @Column(name = "DELETED", nullable = false)
    private Boolean deleted;

}
