package com.citek.mr.web.rest;

import com.citek.mr.JoggingApp;

import com.citek.mr.domain.Jogging;
import com.citek.mr.repository.JoggingRepository;
import com.citek.mr.service.JoggingService;
import com.citek.mr.repository.search.JoggingSearchRepository;
import com.citek.mr.service.dto.JoggingDTO;
import com.citek.mr.service.mapper.JoggingMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.citek.mr.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the JoggingResource REST controller.
 *
 * @see JoggingResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JoggingApp.class)
public class JoggingResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_START = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_START = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_FINISH = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_FINISH = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private JoggingRepository joggingRepository;

    @Autowired
    private JoggingMapper joggingMapper;

    @Autowired
    private JoggingService joggingService;

    @Autowired
    private JoggingSearchRepository joggingSearchRepository;

    @Autowired
    private JoggingQueryService joggingQueryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restJoggingMockMvc;

    private Jogging jogging;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final JoggingResource joggingResource = new JoggingResource(joggingService, joggingQueryService);
        this.restJoggingMockMvc = MockMvcBuilders.standaloneSetup(joggingResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Jogging createEntity(EntityManager em) {
        Jogging jogging = new Jogging()
            .name(DEFAULT_NAME)
            .start(DEFAULT_START)
            .finish(DEFAULT_FINISH);
        return jogging;
    }

    @Before
    public void initTest() {
        joggingSearchRepository.deleteAll();
        jogging = createEntity(em);
    }

    @Test
    @Transactional
    public void createJogging() throws Exception {
        int databaseSizeBeforeCreate = joggingRepository.findAll().size();

        // Create the Jogging
        JoggingDTO joggingDTO = joggingMapper.toDto(jogging);
        restJoggingMockMvc.perform(post("/api/joggings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(joggingDTO)))
            .andExpect(status().isCreated());

        // Validate the Jogging in the database
        List<Jogging> joggingList = joggingRepository.findAll();
        assertThat(joggingList).hasSize(databaseSizeBeforeCreate + 1);
        Jogging testJogging = joggingList.get(joggingList.size() - 1);
        assertThat(testJogging.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testJogging.getStart()).isEqualTo(DEFAULT_START);
        assertThat(testJogging.getFinish()).isEqualTo(DEFAULT_FINISH);

        // Validate the Jogging in Elasticsearch
        Jogging joggingEs = joggingSearchRepository.findOne(testJogging.getId());
        assertThat(joggingEs).isEqualToComparingFieldByField(testJogging);
    }

    @Test
    @Transactional
    public void createJoggingWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = joggingRepository.findAll().size();

        // Create the Jogging with an existing ID
        jogging.setId(1L);
        JoggingDTO joggingDTO = joggingMapper.toDto(jogging);

        // An entity with an existing ID cannot be created, so this API call must fail
        restJoggingMockMvc.perform(post("/api/joggings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(joggingDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Jogging in the database
        List<Jogging> joggingList = joggingRepository.findAll();
        assertThat(joggingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = joggingRepository.findAll().size();
        // set the field null
        jogging.setName(null);

        // Create the Jogging, which fails.
        JoggingDTO joggingDTO = joggingMapper.toDto(jogging);

        restJoggingMockMvc.perform(post("/api/joggings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(joggingDTO)))
            .andExpect(status().isBadRequest());

        List<Jogging> joggingList = joggingRepository.findAll();
        assertThat(joggingList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllJoggings() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);

        // Get all the joggingList
        restJoggingMockMvc.perform(get("/api/joggings?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jogging.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(DEFAULT_START))))
            .andExpect(jsonPath("$.[*].finish").value(hasItem(sameInstant(DEFAULT_FINISH))));
    }

    @Test
    @Transactional
    public void getJogging() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);

        // Get the jogging
        restJoggingMockMvc.perform(get("/api/joggings/{id}", jogging.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(jogging.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.start").value(sameInstant(DEFAULT_START)))
            .andExpect(jsonPath("$.finish").value(sameInstant(DEFAULT_FINISH)));
    }

    @Test
    @Transactional
    public void getAllJoggingsByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);

        // Get all the joggingList where name equals to DEFAULT_NAME
        defaultJoggingShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the joggingList where name equals to UPDATED_NAME
        defaultJoggingShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllJoggingsByNameIsInShouldWork() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);

        // Get all the joggingList where name in DEFAULT_NAME or UPDATED_NAME
        defaultJoggingShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the joggingList where name equals to UPDATED_NAME
        defaultJoggingShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    public void getAllJoggingsByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);

        // Get all the joggingList where name is not null
        defaultJoggingShouldBeFound("name.specified=true");

        // Get all the joggingList where name is null
        defaultJoggingShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    public void getAllJoggingsByStartIsEqualToSomething() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);

        // Get all the joggingList where start equals to DEFAULT_START
        defaultJoggingShouldBeFound("start.equals=" + DEFAULT_START);

        // Get all the joggingList where start equals to UPDATED_START
        defaultJoggingShouldNotBeFound("start.equals=" + UPDATED_START);
    }

    @Test
    @Transactional
    public void getAllJoggingsByStartIsInShouldWork() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);

        // Get all the joggingList where start in DEFAULT_START or UPDATED_START
        defaultJoggingShouldBeFound("start.in=" + DEFAULT_START + "," + UPDATED_START);

        // Get all the joggingList where start equals to UPDATED_START
        defaultJoggingShouldNotBeFound("start.in=" + UPDATED_START);
    }

    @Test
    @Transactional
    public void getAllJoggingsByStartIsNullOrNotNull() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);

        // Get all the joggingList where start is not null
        defaultJoggingShouldBeFound("start.specified=true");

        // Get all the joggingList where start is null
        defaultJoggingShouldNotBeFound("start.specified=false");
    }

    @Test
    @Transactional
    public void getAllJoggingsByStartIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);

        // Get all the joggingList where start greater than or equals to DEFAULT_START
        defaultJoggingShouldBeFound("start.greaterOrEqualThan=" + DEFAULT_START);

        // Get all the joggingList where start greater than or equals to UPDATED_START
        defaultJoggingShouldNotBeFound("start.greaterOrEqualThan=" + UPDATED_START);
    }

    @Test
    @Transactional
    public void getAllJoggingsByStartIsLessThanSomething() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);

        // Get all the joggingList where start less than or equals to DEFAULT_START
        defaultJoggingShouldNotBeFound("start.lessThan=" + DEFAULT_START);

        // Get all the joggingList where start less than or equals to UPDATED_START
        defaultJoggingShouldBeFound("start.lessThan=" + UPDATED_START);
    }


    @Test
    @Transactional
    public void getAllJoggingsByFinishIsEqualToSomething() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);

        // Get all the joggingList where finish equals to DEFAULT_FINISH
        defaultJoggingShouldBeFound("finish.equals=" + DEFAULT_FINISH);

        // Get all the joggingList where finish equals to UPDATED_FINISH
        defaultJoggingShouldNotBeFound("finish.equals=" + UPDATED_FINISH);
    }

    @Test
    @Transactional
    public void getAllJoggingsByFinishIsInShouldWork() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);

        // Get all the joggingList where finish in DEFAULT_FINISH or UPDATED_FINISH
        defaultJoggingShouldBeFound("finish.in=" + DEFAULT_FINISH + "," + UPDATED_FINISH);

        // Get all the joggingList where finish equals to UPDATED_FINISH
        defaultJoggingShouldNotBeFound("finish.in=" + UPDATED_FINISH);
    }

    @Test
    @Transactional
    public void getAllJoggingsByFinishIsNullOrNotNull() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);

        // Get all the joggingList where finish is not null
        defaultJoggingShouldBeFound("finish.specified=true");

        // Get all the joggingList where finish is null
        defaultJoggingShouldNotBeFound("finish.specified=false");
    }

    @Test
    @Transactional
    public void getAllJoggingsByFinishIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);

        // Get all the joggingList where finish greater than or equals to DEFAULT_FINISH
        defaultJoggingShouldBeFound("finish.greaterOrEqualThan=" + DEFAULT_FINISH);

        // Get all the joggingList where finish greater than or equals to UPDATED_FINISH
        defaultJoggingShouldNotBeFound("finish.greaterOrEqualThan=" + UPDATED_FINISH);
    }

    @Test
    @Transactional
    public void getAllJoggingsByFinishIsLessThanSomething() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);

        // Get all the joggingList where finish less than or equals to DEFAULT_FINISH
        defaultJoggingShouldNotBeFound("finish.lessThan=" + DEFAULT_FINISH);

        // Get all the joggingList where finish less than or equals to UPDATED_FINISH
        defaultJoggingShouldBeFound("finish.lessThan=" + UPDATED_FINISH);
    }


    /**
     * Executes the search, and checks that the default entity is returned
     */
    private void defaultJoggingShouldBeFound(String filter) throws Exception {
        restJoggingMockMvc.perform(get("/api/joggings?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jogging.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(DEFAULT_START))))
            .andExpect(jsonPath("$.[*].finish").value(hasItem(sameInstant(DEFAULT_FINISH))));
    }

    /**
     * Executes the search, and checks that the default entity is not returned
     */
    private void defaultJoggingShouldNotBeFound(String filter) throws Exception {
        restJoggingMockMvc.perform(get("/api/joggings?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    @Transactional
    public void getNonExistingJogging() throws Exception {
        // Get the jogging
        restJoggingMockMvc.perform(get("/api/joggings/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateJogging() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);
        joggingSearchRepository.save(jogging);
        int databaseSizeBeforeUpdate = joggingRepository.findAll().size();

        // Update the jogging
        Jogging updatedJogging = joggingRepository.findOne(jogging.getId());
        updatedJogging
            .name(UPDATED_NAME)
            .start(UPDATED_START)
            .finish(UPDATED_FINISH);
        JoggingDTO joggingDTO = joggingMapper.toDto(updatedJogging);

        restJoggingMockMvc.perform(put("/api/joggings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(joggingDTO)))
            .andExpect(status().isOk());

        // Validate the Jogging in the database
        List<Jogging> joggingList = joggingRepository.findAll();
        assertThat(joggingList).hasSize(databaseSizeBeforeUpdate);
        Jogging testJogging = joggingList.get(joggingList.size() - 1);
        assertThat(testJogging.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testJogging.getStart()).isEqualTo(UPDATED_START);
        assertThat(testJogging.getFinish()).isEqualTo(UPDATED_FINISH);

        // Validate the Jogging in Elasticsearch
        Jogging joggingEs = joggingSearchRepository.findOne(testJogging.getId());
        assertThat(joggingEs).isEqualToComparingFieldByField(testJogging);
    }

    @Test
    @Transactional
    public void updateNonExistingJogging() throws Exception {
        int databaseSizeBeforeUpdate = joggingRepository.findAll().size();

        // Create the Jogging
        JoggingDTO joggingDTO = joggingMapper.toDto(jogging);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restJoggingMockMvc.perform(put("/api/joggings")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(joggingDTO)))
            .andExpect(status().isCreated());

        // Validate the Jogging in the database
        List<Jogging> joggingList = joggingRepository.findAll();
        assertThat(joggingList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteJogging() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);
        joggingSearchRepository.save(jogging);
        int databaseSizeBeforeDelete = joggingRepository.findAll().size();

        // Get the jogging
        restJoggingMockMvc.perform(delete("/api/joggings/{id}", jogging.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean joggingExistsInEs = joggingSearchRepository.exists(jogging.getId());
        assertThat(joggingExistsInEs).isFalse();

        // Validate the database is empty
        List<Jogging> joggingList = joggingRepository.findAll();
        assertThat(joggingList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchJogging() throws Exception {
        // Initialize the database
        joggingRepository.saveAndFlush(jogging);
        joggingSearchRepository.save(jogging);

        // Search the jogging
        restJoggingMockMvc.perform(get("/api/_search/joggings?query=id:" + jogging.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(jogging.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].start").value(hasItem(sameInstant(DEFAULT_START))))
            .andExpect(jsonPath("$.[*].finish").value(hasItem(sameInstant(DEFAULT_FINISH))));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Jogging.class);
        Jogging jogging1 = new Jogging();
        jogging1.setId(1L);
        Jogging jogging2 = new Jogging();
        jogging2.setId(jogging1.getId());
        assertThat(jogging1).isEqualTo(jogging2);
        jogging2.setId(2L);
        assertThat(jogging1).isNotEqualTo(jogging2);
        jogging1.setId(null);
        assertThat(jogging1).isNotEqualTo(jogging2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(JoggingDTO.class);
        JoggingDTO joggingDTO1 = new JoggingDTO();
        joggingDTO1.setId(1L);
        JoggingDTO joggingDTO2 = new JoggingDTO();
        assertThat(joggingDTO1).isNotEqualTo(joggingDTO2);
        joggingDTO2.setId(joggingDTO1.getId());
        assertThat(joggingDTO1).isEqualTo(joggingDTO2);
        joggingDTO2.setId(2L);
        assertThat(joggingDTO1).isNotEqualTo(joggingDTO2);
        joggingDTO1.setId(null);
        assertThat(joggingDTO1).isNotEqualTo(joggingDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(joggingMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(joggingMapper.fromId(null)).isNull();
    }
}
