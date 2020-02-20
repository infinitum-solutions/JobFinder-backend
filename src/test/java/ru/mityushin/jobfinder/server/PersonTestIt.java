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
import ru.mityushin.jobfinder.server.dto.PersonDTO;
import ru.mityushin.jobfinder.server.util.enums.Sex;

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
public class PersonTestIt extends BaseIntegrationTest {

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
    public void getPersonsWithoutAuthorize() throws Exception {
        mockMvc.perform(get("/api/persons")
                .secure(true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    public void getAllPersons() throws Exception {
        mockMvc.perform(get("/api/persons")
                .secure(true))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void createPersonWithoutAuthorize() throws Exception {
        mockMvc.perform(post("/api/persons")
                .secure(true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void updatePersonWithoutAuthorize() throws Exception {
        mockMvc.perform(put("/api/persons")
                .secure(true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void deletePersonWithoutAuthorize() throws Exception {
        mockMvc.perform(delete("/api/persons")
                .secure(true))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @WithMockUser(username = "admin", password = "password", roles = "ADMIN")
    public void createUpdateDeleteOrganization() throws Exception {
        PersonDTO personDTO = PersonDTO.builder()
                .username("test")
                .password("password")
                .firstName("For")
                .lastName("Tests")
                .country("Russia")
                .sex(Sex.MALE)
                .build();
        String json = mapper.writeValueAsString(personDTO);
        ResultActions saveResultActions = mockMvc.perform(post("/api/persons")
                .secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.uuid", notNullValue()));
        PersonDTO saved = mapper.readValue(saveResultActions.andReturn().getResponse().getContentAsString(), PersonDTO.class);
        String uuid = saved.getUuid().toString();
        PersonDTO organizationToUpdate = PersonDTO.builder()
                .uuid(saved.getUuid())
                .username(saved.getUsername().concat(" updated"))
                .password(saved.getPassword())
                .firstName(saved.getFirstName().concat(" updated"))
                .lastName(saved.getLastName().concat(" updated"))
                .country(saved.getCountry().concat(" updated"))
                .sex(saved.getSex())
                .build();
        ResultActions putResultActions = mockMvc.perform(put("/api/persons/".concat(uuid))
                .secure(true)
                .content(mapper.writeValueAsString(organizationToUpdate))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapper.writeValueAsString(organizationToUpdate)))
                .andExpect(jsonPath("$.uuid", notNullValue()));
        PersonDTO updated = mapper.readValue(putResultActions.andReturn().getResponse().getContentAsString(), PersonDTO.class);
        assertEquals(organizationToUpdate, updated);
        ResultActions deleteResultActions = mockMvc.perform(delete("/api/persons/".concat(uuid))
                .secure(true))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().json(mapper.writeValueAsString(updated)))
                .andExpect(jsonPath("$.uuid", notNullValue()));
        PersonDTO deleted = mapper.readValue(putResultActions.andReturn().getResponse().getContentAsString(), PersonDTO.class);
        assertEquals(updated, deleted);
    }
}
