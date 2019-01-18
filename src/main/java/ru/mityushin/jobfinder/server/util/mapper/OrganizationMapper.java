package ru.mityushin.jobfinder.server.util.mapper;

import ru.mityushin.jobfinder.server.model.Organization;
import ru.mityushin.jobfinder.server.dto.OrganizationDTO;

import javax.validation.constraints.NotNull;

public class OrganizationMapper {

    public static Organization map(@NotNull OrganizationDTO organizationDTO) {
        return Organization.builder()
                .uuid(organizationDTO.getUuid())
                .creatorUuid(organizationDTO.getCreatorUuid())
                .title(organizationDTO.getTitle())
                .description(organizationDTO.getDescription())
                .build();
    }

    public static OrganizationDTO map(@NotNull Organization organization) {
        Integer subscribersCount = organization.getSubscribers() == null ? 0 : organization.getSubscribers().size();
        return OrganizationDTO.builder()
                .uuid(organization.getUuid())
                .creatorUuid(organization.getCreatorUuid())
                .title(organization.getTitle())
                .description(organization.getDescription())
                .subscribersCount(subscribersCount)
                .build();
    }
}
