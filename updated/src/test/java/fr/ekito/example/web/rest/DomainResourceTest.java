package fr.ekito.example.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import fr.ekito.example.Application;
import fr.ekito.example.domain.Domain;
import fr.ekito.example.repository.DomainRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DomainResource REST controller.
 *
 * @see DomainResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class DomainResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";


    @Inject
    private DomainRepository domainRepository;

    private MockMvc restDomainMockMvc;

    private Domain domain;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DomainResource domainResource = new DomainResource();
        ReflectionTestUtils.setField(domainResource, "domainRepository", domainRepository);
        this.restDomainMockMvc = MockMvcBuilders.standaloneSetup(domainResource).build();
    }

    @Before
    public void initTest() {
        domainRepository.deleteAll();
        domain = new Domain(DEFAULT_NAME);
    }

    @Test
    public void createDomain() throws Exception {
        // Validate the database is empty
        assertThat(domainRepository.findAll()).hasSize(0);

        // Create the Domain
        restDomainMockMvc.perform(post("/app/rest/domains")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(domain)))
                .andExpect(status().isOk());

        // Validate the Domain in the database
        List<Domain> domains = domainRepository.findAll();
        assertThat(domains).hasSize(1);
        Domain testDomain = domains.iterator().next();
        assertThat(testDomain.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    public void getAllDomains() throws Exception {
        // Initialize the database
        domainRepository.save(domain);

        // Get all the domains
        restDomainMockMvc.perform(get("/app/rest/domains"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value(domain.getId()))
                .andExpect(jsonPath("$.[0].name").value(DEFAULT_NAME.toString()));
    }

    @Test
    public void getDomain() throws Exception {
        // Initialize the database
        domainRepository.save(domain);

        // Get the domain
        restDomainMockMvc.perform(get("/app/rest/domains/{id}", domain.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(domain.getId()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    public void getNonExistingDomain() throws Exception {
        // Get the domain
        restDomainMockMvc.perform(get("/app/rest/domains/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateDomain() throws Exception {
        // Initialize the database
        domainRepository.save(domain);

        // Update the domain
        domain.setName(UPDATED_NAME);
        restDomainMockMvc.perform(post("/app/rest/domains")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(domain)))
                .andExpect(status().isOk());

        // Validate the Domain in the database
        List<Domain> domains = domainRepository.findAll();
        assertThat(domains).hasSize(1);
        Domain testDomain = domains.iterator().next();
        assertThat(testDomain.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    public void deleteDomain() throws Exception {
        // Initialize the database
        domainRepository.save(domain);

        // Get the domain
        restDomainMockMvc.perform(delete("/app/rest/domains/{id}", domain.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Domain> domains = domainRepository.findAll();
        assertThat(domains).hasSize(0);
    }
}
