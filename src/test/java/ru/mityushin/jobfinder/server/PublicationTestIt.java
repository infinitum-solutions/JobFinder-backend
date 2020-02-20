package ru.mityushin.jobfinder.server;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.mityushin.jobfinder.server.dto.PublicationDTO;

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
public class PublicationTestIt extends BaseIntegrationTest {

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
    public void getAllPublicationsWithoutAuthorize() throws Exception {
        mockMvc.perform(get("/api/publications")
                .secure(true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    public void getAllPublications() throws Exception {
        mockMvc.perform(get("/api/publications")
                .secure(true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void createPublicationWithoutAuthorize() throws Exception {
        mockMvc.perform(post("/api/publications")
                .secure(true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updatePublicationWithoutAuthorize() throws Exception {
        mockMvc.perform(put("/api/publications")
                .secure(true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void deletePublicationWithoutAuthorize() throws Exception {
        mockMvc.perform(delete("/api/publications")
                .secure(true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    public void createUpdateDeletePublication() throws Exception {
        PublicationDTO publicationDTO = PublicationDTO.builder()
                .title("Title")
                .content("content")
                .build();
        String json = mapper.writeValueAsString(publicationDTO);
        ResultActions saveResultActions = mockMvc.perform(post("/api/publications")
                .secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.uuid", notNullValue()));
        PublicationDTO saved = mapper.readValue(saveResultActions.andReturn().getResponse().getContentAsString(), PublicationDTO.class);
        String uuid = saved.getUuid().toString();
        PublicationDTO publicationToUpdate = PublicationDTO.builder()
                .uuid(saved.getUuid())
                .title(saved.getTitle())
                .description("updated")
                .authorUuid(saved.getAuthorUuid())
                .content("content 2")
                .build();
        ResultActions putResultActions = mockMvc.perform(put("/api/publications/".concat(uuid))
                .secure(true)
                .content(mapper.writeValueAsString(publicationToUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapper.writeValueAsString(publicationToUpdate)))
                .andExpect(jsonPath("$.uuid", notNullValue()));
        PublicationDTO updated = mapper.readValue(putResultActions.andReturn().getResponse().getContentAsString(), PublicationDTO.class);
        assertEquals(publicationToUpdate, updated);
        ResultActions deleteResultActions = mockMvc.perform(delete("/api/publications/".concat(uuid))
                .secure(true))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapper.writeValueAsString(updated)))
                .andExpect(jsonPath("$.uuid", notNullValue()));
        PublicationDTO deleted = mapper.readValue(putResultActions.andReturn().getResponse().getContentAsString(), PublicationDTO.class);
        assertEquals(updated, deleted);
    }
}