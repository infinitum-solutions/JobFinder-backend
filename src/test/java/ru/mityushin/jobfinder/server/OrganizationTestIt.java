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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.Base64Utils;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.mityushin.jobfinder.server.BaseIntegrationTest;
import ru.mityushin.jobfinder.server.dto.OrganizationDTO;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(loader = AnnotationConfigWebContextLoader.class)
public class OrganizationTestIt extends BaseIntegrationTest {

    private final String authHeader = "Basic " + Base64Utils.encodeToString("user:password".getBytes());

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
    public void getAllOrganizations() throws Exception {
        mockMvc.perform(get("/api/organizations")
                .secure(true)
                .header(HttpHeaders.AUTHORIZATION, authHeader))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void createOrganization() throws Exception {
        OrganizationDTO organizationDTO = OrganizationDTO.builder()
                .title("Title")
                .build();
        mockMvc.perform(post("/api/organizations")
                .secure(true)
                .header(HttpHeaders.AUTHORIZATION, authHeader)
                .content(mapper.writeValueAsString(organizationDTO))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.uuid", notNullValue()));
    }
}