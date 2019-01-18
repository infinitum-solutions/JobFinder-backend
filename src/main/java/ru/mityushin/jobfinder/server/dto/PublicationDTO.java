package ru.mityushin.jobfinder.server.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Builder
@Getter
public class PublicationDTO {

    private UUID uuid;
    private UUID authorUuid;
    @NotNull
    @Size(min = 3, message = "Title length must be more than 3.")
    private String title;
    private String description;
    @NotNull
    @Size(min = 3, message = "Content length must be more than 3.")
    private String content;
    private Boolean visible;
}
