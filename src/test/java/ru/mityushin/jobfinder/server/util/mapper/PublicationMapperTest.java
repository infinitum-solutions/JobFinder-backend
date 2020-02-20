package ru.mityushin.jobfinder.server.util.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.mityushin.jobfinder.server.dto.PublicationDTO;
import ru.mityushin.jobfinder.server.model.Publication;

import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class PublicationMapperTest {
    private static final UUID DEFAULT_UUID = UUID.fromString("01234567-89ab-cdef-0123-456789abcdef");
    private Publication defaultPublication;
    private PublicationDTO defaultPublicationDTO;

    @Before
    public void before() {
        defaultPublication = Publication.builder()
                .id(1L)
                .uuid(DEFAULT_UUID)
                .authorUuid(DEFAULT_UUID)
                .title("Title")
                .description("description")
                .content("Content-content-content")
                .visible(true)
                .deleted(false)
                .build();
        defaultPublicationDTO = PublicationDTO.builder()
                .uuid(DEFAULT_UUID)
                .authorUuid(DEFAULT_UUID)
                .title("Title")
                .description("description")
                .content("Content-content-content")
                .visible(true)
                .build();
    }

    @Test
    public void mapFromDto() {
        Publication publication = PublicationMapper.map(defaultPublicationDTO);
        assertNotEquals(defaultPublication, publication);
    }

    @Test
    public void mapToDto() {
        PublicationDTO publicationDTO = PublicationMapper.map(defaultPublication);
        assertEquals(defaultPublicationDTO, publicationDTO);
    }
}