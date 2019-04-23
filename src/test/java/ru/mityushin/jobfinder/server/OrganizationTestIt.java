package ru.mityushin.jobfinder.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.mityushin.jobfinder.server.dto.OrganizationDTO;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(loader = AnnotationConfigWebContextLoader.class)
public class OrganizationTestIt extends BaseIntegrationTest {

    private final String authHeader = "Basic " + Base64Utils.encodeToString("admin:password".getBytes());

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @TestConfiguration
    static class ContextConfiguration {
        @Bean
        public MockMvc mockMvc(WebApplicationContext wac) {
            return webAppContextSetup(wac).apply(springSecurity()).build();
        }

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    @Before
    public void before() {
//        mockMvc.perform(post("/login"))
    }

    @Test
    public void getAllOrganizationsWithoutAuthorize() throws Exception {
        mockMvc.perform(get("/api/organizations")
                .secure(true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    public void getAllOrganizations() throws Exception {
        mockMvc.perform(get("/api/organizations")
                .secure(true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void createOrganizationWithoutAuthorize() throws Exception {
        mockMvc.perform(post("/api/organizations")
                .secure(true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updateOrganizationWithoutAuthorize() throws Exception {
        mockMvc.perform(put("/api/organizations")
                .secure(true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void deleteOrganizationWithoutAuthorize() throws Exception {
        mockMvc.perform(delete("/api/organizations")
                .secure(true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createUpdateDeleteOrganization() throws Exception {
        OrganizationDTO organizationDTO = OrganizationDTO.builder()
                .title("Title")
                .build();
        String json = mapper.writeValueAsString(organizationDTO);
        ResultActions saveResultActions = mockMvc.perform(post("/api/organizations")
                .secure(true)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.uuid", notNullValue()));
        OrganizationDTO saved = mapper.readValue(saveResultActions.andReturn().getResponse().getContentAsString(), OrganizationDTO.class);
        String uuid = saved.getUuid().toString();
        OrganizationDTO organizationToUpdate = OrganizationDTO.builder()
                .uuid(saved.getUuid())
                .title(saved.getTitle())
                .description("updated")
                .creatorUuid(saved.getCreatorUuid())
                .subscribersCount(saved.getSubscribersCount())
                .build();
        ResultActions putResultActions = mockMvc.perform(put("/api/organizations/".concat(uuid))
                .secure(true)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .content(mapper.writeValueAsString(organizationToUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapper.writeValueAsString(organizationToUpdate)))
                .andExpect(jsonPath("$.uuid", notNullValue()));
        OrganizationDTO updated = mapper.readValue(putResultActions.andReturn().getResponse().getContentAsString(), OrganizationDTO.class);
        assertEquals(organizationToUpdate, updated);
        ResultActions deleteResultActions = mockMvc.perform(delete("/api/organizations/".concat(uuid))
                .secure(true)
                .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapper.writeValueAsString(updated)))
                .andExpect(jsonPath("$.uuid", notNullValue()));
        OrganizationDTO deleted = mapper.readValue(putResultActions.andReturn().getResponse().getContentAsString(), OrganizationDTO.class);
        assertEquals(updated, deleted);
    }
}