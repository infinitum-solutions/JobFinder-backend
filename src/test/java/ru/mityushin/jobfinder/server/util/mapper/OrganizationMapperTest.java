package ru.mityushin.jobfinder.server.util.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import ru.mityushin.jobfinder.server.dto.OrganizationDTO;
import ru.mityushin.jobfinder.server.model.Organization;

import java.util.HashSet;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class OrganizationMapperTest {
    private static final UUID DEFAULT_UUID = UUID.fromString("01234567-89ab-cdef-0123-456789abcdef");
    private OrganizationDTO defaultOrganizationDTO;
    private Organization defaultOrganization;

    @Before
    public void before() {
        defaultOrganization = Organization.builder()
                .id(1L)
                .uuid(DEFAULT_UUID)
                .creatorUuid(DEFAULT_UUID)
                .title("Title")
                .description("description")
                .deleted(false)
                .subscribers(new HashSet<>())
                .build();
        defaultOrganizationDTO = OrganizationDTO.builder()
                .uuid(DEFAULT_UUID)
                .creatorUuid(DEFAULT_UUID)
                .title("Title")
                .description("description")
                .subscribersCount(0)
                .build();
    }

    @Test
    public void mapFromDto() {
        Organization organization = OrganizationMapper.map(defaultOrganizationDTO);
        assertEquals(defaultOrganization, organization);
    }

    @Test
    public void mapToDto() {
        OrganizationDTO organizationDTO = OrganizationMapper.map(defaultOrganization);
        assertEquals(defaultOrganizationDTO, organizationDTO);
    }
}