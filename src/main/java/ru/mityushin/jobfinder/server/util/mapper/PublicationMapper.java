package ru.mityushin.jobfinder.server.util.mapper;

import ru.mityushin.jobfinder.server.model.Publication;
import ru.mityushin.jobfinder.server.dto.PublicationDTO;

import javax.validation.constraints.NotNull;

public class PublicationMapper {

    @NotNull
    public static Publication map(PublicationDTO publicationDTO) {
        return Publication.builder()
                .uuid(publicationDTO.getUuid())
                .authorUuid(publicationDTO.getAuthorUuid())
                .title(publicationDTO.getTitle())
                .description(publicationDTO.getDescription())
                .content(publicationDTO.getContent())
                .visible(publicationDTO.getVisible())
                .build();
    }

    @NotNull
    public static PublicationDTO map(Publication publication) {
        return PublicationDTO.builder()
                .uuid(publication.getUuid())
                .authorUuid(publication.getAuthorUuid())
                .title(publication.getTitle())
                .description(publication.getDescription())
                .content(publication.getContent())
                .visible(publication.getVisible())
                .build();
    }

}
