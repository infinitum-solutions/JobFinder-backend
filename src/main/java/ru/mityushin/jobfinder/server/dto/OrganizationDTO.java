package ru.mityushin.jobfinder.server.dto;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

@Builder
@Getter
public class OrganizationDTO {
    private UUID uuid;
    private UUID creatorUuid;
    @NotNull
    @Size(min = 3, message = "Title length must be more than 3.")
    private String title;
    private String description;
    private Integer subscribersCount;
}
