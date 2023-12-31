package com.jhipster.health.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jhipster.health.IntegrationTest;
import com.jhipster.health.domain.BloodPressure;
import com.jhipster.health.domain.User;
import com.jhipster.health.repository.BloodPressureRepository;
import com.jhipster.health.repository.UserRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BloodPressureResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class BloodPressureResourceIT {

    private static final Instant DEFAULT_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Float DEFAULT_SYSTOLIC = 1F;
    private static final Float UPDATED_SYSTOLIC = 2F;

    private static final Float DEFAULT_DIASTOLIC = 1F;
    private static final Float UPDATED_DIASTOLIC = 2F;

    private static final String ENTITY_API_URL = "/api/blood-pressures";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BloodPressureRepository bloodPressureRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private BloodPressureRepository bloodPressureRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBloodPressureMockMvc;

    private BloodPressure bloodPressure;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BloodPressure createEntity(EntityManager em) {
        BloodPressure bloodPressure = new BloodPressure()
            .date(DEFAULT_DATE.atZone(ZoneId.of("Asia/Dhaka")))
            .systolic(DEFAULT_SYSTOLIC)
            .diastolic(DEFAULT_DIASTOLIC);
        return bloodPressure;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BloodPressure createUpdatedEntity(EntityManager em) {
        BloodPressure bloodPressure = new BloodPressure()
            .date(UPDATED_DATE.atZone(ZoneId.of("Asia/Dhaka")))
            .systolic(UPDATED_SYSTOLIC)
            .diastolic(UPDATED_DIASTOLIC);
        return bloodPressure;
    }

    private void createBloodPressureByMonth(ZonedDateTime firstDate, ZonedDateTime firstDayOfLastMonth) {
        User user = userRepository.findOneByLogin("user").get();

        bloodPressure = new BloodPressure().date(firstDate).systolic(120f).diastolic(80f).user(user);
        bloodPressureRepository.saveAndFlush(bloodPressure);
        bloodPressure = new BloodPressure().date(firstDate.plusDays(10)).systolic(125f).diastolic(75f).user(user);
        bloodPressureRepository.saveAndFlush(bloodPressure);
        bloodPressure = new BloodPressure().date(firstDate.plusDays(20)).systolic(100f).diastolic(69f).user(user);
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // last month
        bloodPressure = new BloodPressure().date(firstDayOfLastMonth).systolic(130f).diastolic(90f).user(user);
        bloodPressureRepository.saveAndFlush(bloodPressure);
        bloodPressure = new BloodPressure().date(firstDayOfLastMonth.plusDays(11)).systolic(135f).diastolic(85f).user(user);
        bloodPressureRepository.saveAndFlush(bloodPressure);
        bloodPressure = new BloodPressure().date(firstDayOfLastMonth.plusDays(23)).systolic(130f).diastolic(75f).user(user);
        bloodPressureRepository.saveAndFlush(bloodPressure);
    }

    @BeforeEach
    public void initTest() {
        bloodPressure = createEntity(em);
    }

    @Test
    @Transactional
    public void getBloodPressureForLast30Days() throws Exception {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime twentyNineDaysAgo = now.minusDays(29);
        ZonedDateTime firstDayOfLastMonth = now.withDayOfMonth(1).minusMonths(1);
        System.out.println("29 - " + twentyNineDaysAgo + "    firstdayOfLastMonth - " + firstDayOfLastMonth);
        createBloodPressureByMonth(twentyNineDaysAgo, firstDayOfLastMonth);

        // Get all the blood pressure reading
        restBloodPressureMockMvc
            .perform(get("/api/blood-pressures"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$", hasSize(6)));

        // Get the blood pressure readings for the last 30 days
        restBloodPressureMockMvc
            .perform(get("/api/bp-by-days/{days}", 30))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.period").value("Last 30 Days"))
            .andExpect(jsonPath("$.readings.[*].systolic").value(hasItem(120.0)))
            .andExpect(jsonPath("$.readings.[*].diastolic").value(hasItem(69.0)));
    }

    @Test
    @Transactional
    void createBloodPressure() throws Exception {
        int databaseSizeBeforeCreate = bloodPressureRepository.findAll().size();
        // Create the BloodPressure
        restBloodPressureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bloodPressure)))
            .andExpect(status().isCreated());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeCreate + 1);
        BloodPressure testBloodPressure = bloodPressureList.get(bloodPressureList.size() - 1);
        assertThat(testBloodPressure.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testBloodPressure.getSystolic()).isEqualTo(DEFAULT_SYSTOLIC);
        assertThat(testBloodPressure.getDiastolic()).isEqualTo(DEFAULT_DIASTOLIC);
    }

    @Test
    @Transactional
    void createBloodPressureWithExistingId() throws Exception {
        // Create the BloodPressure with an existing ID
        bloodPressure.setId(1L);

        int databaseSizeBeforeCreate = bloodPressureRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBloodPressureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bloodPressure)))
            .andExpect(status().isBadRequest());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = bloodPressureRepository.findAll().size();
        // set the field null
        bloodPressure.setDate(null);

        // Create the BloodPressure, which fails.

        restBloodPressureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bloodPressure)))
            .andExpect(status().isBadRequest());

        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSystolicIsRequired() throws Exception {
        int databaseSizeBeforeTest = bloodPressureRepository.findAll().size();
        // set the field null
        bloodPressure.setSystolic(null);

        // Create the BloodPressure, which fails.

        restBloodPressureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bloodPressure)))
            .andExpect(status().isBadRequest());

        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDiastolicIsRequired() throws Exception {
        int databaseSizeBeforeTest = bloodPressureRepository.findAll().size();
        // set the field null
        bloodPressure.setDiastolic(null);

        // Create the BloodPressure, which fails.

        restBloodPressureMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bloodPressure)))
            .andExpect(status().isBadRequest());

        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBloodPressures() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get all the bloodPressureList
        restBloodPressureMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bloodPressure.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())))
            .andExpect(jsonPath("$.[*].systolic").value(hasItem(DEFAULT_SYSTOLIC.doubleValue())))
            .andExpect(jsonPath("$.[*].diastolic").value(hasItem(DEFAULT_DIASTOLIC.doubleValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBloodPressuresWithEagerRelationshipsIsEnabled() throws Exception {
        when(bloodPressureRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBloodPressureMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(bloodPressureRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllBloodPressuresWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(bloodPressureRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restBloodPressureMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(bloodPressureRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getBloodPressure() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        // Get the bloodPressure
        restBloodPressureMockMvc
            .perform(get(ENTITY_API_URL_ID, bloodPressure.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bloodPressure.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()))
            .andExpect(jsonPath("$.systolic").value(DEFAULT_SYSTOLIC.doubleValue()))
            .andExpect(jsonPath("$.diastolic").value(DEFAULT_DIASTOLIC.doubleValue()));
    }

    @Test
    @Transactional
    void getNonExistingBloodPressure() throws Exception {
        // Get the bloodPressure
        restBloodPressureMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBloodPressure() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        int databaseSizeBeforeUpdate = bloodPressureRepository.findAll().size();

        // Update the bloodPressure
        BloodPressure updatedBloodPressure = bloodPressureRepository.findById(bloodPressure.getId()).get();
        // Disconnect from session so that the updates on updatedBloodPressure are not directly saved in db
        em.detach(updatedBloodPressure);
        updatedBloodPressure.date(UPDATED_DATE.atZone(ZoneId.of("Asia/Dhaka"))).systolic(UPDATED_SYSTOLIC).diastolic(UPDATED_DIASTOLIC);

        restBloodPressureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBloodPressure.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBloodPressure))
            )
            .andExpect(status().isOk());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeUpdate);
        BloodPressure testBloodPressure = bloodPressureList.get(bloodPressureList.size() - 1);
        assertThat(testBloodPressure.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testBloodPressure.getSystolic()).isEqualTo(UPDATED_SYSTOLIC);
        assertThat(testBloodPressure.getDiastolic()).isEqualTo(UPDATED_DIASTOLIC);
    }

    @Test
    @Transactional
    void putNonExistingBloodPressure() throws Exception {
        int databaseSizeBeforeUpdate = bloodPressureRepository.findAll().size();
        bloodPressure.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBloodPressureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bloodPressure.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bloodPressure))
            )
            .andExpect(status().isBadRequest());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBloodPressure() throws Exception {
        int databaseSizeBeforeUpdate = bloodPressureRepository.findAll().size();
        bloodPressure.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBloodPressureMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(bloodPressure))
            )
            .andExpect(status().isBadRequest());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBloodPressure() throws Exception {
        int databaseSizeBeforeUpdate = bloodPressureRepository.findAll().size();
        bloodPressure.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBloodPressureMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(bloodPressure)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBloodPressureWithPatch() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        int databaseSizeBeforeUpdate = bloodPressureRepository.findAll().size();

        // Update the bloodPressure using partial update
        BloodPressure partialUpdatedBloodPressure = new BloodPressure();
        partialUpdatedBloodPressure.setId(bloodPressure.getId());

        partialUpdatedBloodPressure.diastolic(UPDATED_DIASTOLIC);

        restBloodPressureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBloodPressure.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBloodPressure))
            )
            .andExpect(status().isOk());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeUpdate);
        BloodPressure testBloodPressure = bloodPressureList.get(bloodPressureList.size() - 1);
        assertThat(testBloodPressure.getDate()).isEqualTo(DEFAULT_DATE);
        assertThat(testBloodPressure.getSystolic()).isEqualTo(DEFAULT_SYSTOLIC);
        assertThat(testBloodPressure.getDiastolic()).isEqualTo(UPDATED_DIASTOLIC);
    }

    @Test
    @Transactional
    void fullUpdateBloodPressureWithPatch() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        int databaseSizeBeforeUpdate = bloodPressureRepository.findAll().size();

        // Update the bloodPressure using partial update
        BloodPressure partialUpdatedBloodPressure = new BloodPressure();
        partialUpdatedBloodPressure.setId(bloodPressure.getId());

        partialUpdatedBloodPressure
            .date(UPDATED_DATE.atZone(ZoneId.of("Asia/Dhaka")))
            .systolic(UPDATED_SYSTOLIC)
            .diastolic(UPDATED_DIASTOLIC);

        restBloodPressureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBloodPressure.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBloodPressure))
            )
            .andExpect(status().isOk());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeUpdate);
        BloodPressure testBloodPressure = bloodPressureList.get(bloodPressureList.size() - 1);
        assertThat(testBloodPressure.getDate()).isEqualTo(UPDATED_DATE);
        assertThat(testBloodPressure.getSystolic()).isEqualTo(UPDATED_SYSTOLIC);
        assertThat(testBloodPressure.getDiastolic()).isEqualTo(UPDATED_DIASTOLIC);
    }

    @Test
    @Transactional
    void patchNonExistingBloodPressure() throws Exception {
        int databaseSizeBeforeUpdate = bloodPressureRepository.findAll().size();
        bloodPressure.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBloodPressureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bloodPressure.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bloodPressure))
            )
            .andExpect(status().isBadRequest());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBloodPressure() throws Exception {
        int databaseSizeBeforeUpdate = bloodPressureRepository.findAll().size();
        bloodPressure.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBloodPressureMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(bloodPressure))
            )
            .andExpect(status().isBadRequest());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBloodPressure() throws Exception {
        int databaseSizeBeforeUpdate = bloodPressureRepository.findAll().size();
        bloodPressure.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBloodPressureMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(bloodPressure))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the BloodPressure in the database
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBloodPressure() throws Exception {
        // Initialize the database
        bloodPressureRepository.saveAndFlush(bloodPressure);

        int databaseSizeBeforeDelete = bloodPressureRepository.findAll().size();

        // Delete the bloodPressure
        restBloodPressureMockMvc
            .perform(delete(ENTITY_API_URL_ID, bloodPressure.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<BloodPressure> bloodPressureList = bloodPressureRepository.findAll();
        assertThat(bloodPressureList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
