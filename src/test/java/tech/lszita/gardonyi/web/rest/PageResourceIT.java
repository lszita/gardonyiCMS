package tech.lszita.gardonyi.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tech.lszita.gardonyi.IntegrationTest;
import tech.lszita.gardonyi.domain.PageEntity;
import tech.lszita.gardonyi.repository.PageRepository;
import tech.lszita.gardonyi.service.dto.PageDTO;
import tech.lszita.gardonyi.service.mapper.PageMapper;

/**
 * Integration tests for the {@link PageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class PageResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private PageMapper pageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPageMockMvc;

    private PageEntity page;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PageEntity createEntity(EntityManager em) {
        PageEntity page = new PageEntity().title(DEFAULT_TITLE).content(DEFAULT_CONTENT);
        return page;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PageEntity createUpdatedEntity(EntityManager em) {
        PageEntity page = new PageEntity().title(UPDATED_TITLE).content(UPDATED_CONTENT);
        return page;
    }

    @BeforeEach
    public void initTest() {
        page = createEntity(em);
    }

    @Test
    @Transactional
    void createPage() throws Exception {
        int databaseSizeBeforeCreate = pageRepository.findAll().size();
        // Create the Page
        PageDTO pageDTO = pageMapper.toDto(page);
        restPageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pageDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Page in the database
        List<PageEntity> pageList = pageRepository.findAll();
        assertThat(pageList).hasSize(databaseSizeBeforeCreate + 1);
        PageEntity testPage = pageList.get(pageList.size() - 1);
        assertThat(testPage.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPage.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void createPageWithExistingId() throws Exception {
        // Create the Page with an existing ID
        page.setId(1L);
        PageDTO pageDTO = pageMapper.toDto(page);

        int databaseSizeBeforeCreate = pageRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPageMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Page in the database
        List<PageEntity> pageList = pageRepository.findAll();
        assertThat(pageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllPages() throws Exception {
        // Initialize the database
        pageRepository.saveAndFlush(page);

        // Get all the pageList
        restPageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(page.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)));
    }

    @Test
    @Transactional
    void getPage() throws Exception {
        // Initialize the database
        pageRepository.saveAndFlush(page);

        // Get the page
        restPageMockMvc
            .perform(get(ENTITY_API_URL_ID, page.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(page.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT));
    }

    @Test
    @Transactional
    void getNonExistingPage() throws Exception {
        // Get the page
        restPageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPage() throws Exception {
        // Initialize the database
        pageRepository.saveAndFlush(page);

        int databaseSizeBeforeUpdate = pageRepository.findAll().size();

        // Update the page
        PageEntity updatedPage = pageRepository.findById(page.getId()).get();
        // Disconnect from session so that the updates on updatedPage are not directly saved in db
        em.detach(updatedPage);
        updatedPage.title(UPDATED_TITLE).content(UPDATED_CONTENT);
        PageDTO pageDTO = pageMapper.toDto(updatedPage);

        restPageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pageDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pageDTO))
            )
            .andExpect(status().isOk());

        // Validate the Page in the database
        List<PageEntity> pageList = pageRepository.findAll();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
        PageEntity testPage = pageList.get(pageList.size() - 1);
        assertThat(testPage.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPage.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void putNonExistingPage() throws Exception {
        int databaseSizeBeforeUpdate = pageRepository.findAll().size();
        page.setId(count.incrementAndGet());

        // Create the Page
        PageDTO pageDTO = pageMapper.toDto(page);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pageDTO.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Page in the database
        List<PageEntity> pageList = pageRepository.findAll();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPage() throws Exception {
        int databaseSizeBeforeUpdate = pageRepository.findAll().size();
        page.setId(count.incrementAndGet());

        // Create the Page
        PageDTO pageDTO = pageMapper.toDto(page);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(pageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Page in the database
        List<PageEntity> pageList = pageRepository.findAll();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPage() throws Exception {
        int databaseSizeBeforeUpdate = pageRepository.findAll().size();
        page.setId(count.incrementAndGet());

        // Create the Page
        PageDTO pageDTO = pageMapper.toDto(page);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPageMockMvc
            .perform(
                put(ENTITY_API_URL).with(csrf()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(pageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Page in the database
        List<PageEntity> pageList = pageRepository.findAll();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePageWithPatch() throws Exception {
        // Initialize the database
        pageRepository.saveAndFlush(page);

        int databaseSizeBeforeUpdate = pageRepository.findAll().size();

        // Update the page using partial update
        PageEntity partialUpdatedPage = new PageEntity();
        partialUpdatedPage.setId(page.getId());

        partialUpdatedPage.content(UPDATED_CONTENT);

        restPageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPage))
            )
            .andExpect(status().isOk());

        // Validate the Page in the database
        List<PageEntity> pageList = pageRepository.findAll();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
        PageEntity testPage = pageList.get(pageList.size() - 1);
        assertThat(testPage.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPage.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void fullUpdatePageWithPatch() throws Exception {
        // Initialize the database
        pageRepository.saveAndFlush(page);

        int databaseSizeBeforeUpdate = pageRepository.findAll().size();

        // Update the page using partial update
        PageEntity partialUpdatedPage = new PageEntity();
        partialUpdatedPage.setId(page.getId());

        partialUpdatedPage.title(UPDATED_TITLE).content(UPDATED_CONTENT);

        restPageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPage.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedPage))
            )
            .andExpect(status().isOk());

        // Validate the Page in the database
        List<PageEntity> pageList = pageRepository.findAll();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
        PageEntity testPage = pageList.get(pageList.size() - 1);
        assertThat(testPage.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPage.getContent()).isEqualTo(UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void patchNonExistingPage() throws Exception {
        int databaseSizeBeforeUpdate = pageRepository.findAll().size();
        page.setId(count.incrementAndGet());

        // Create the Page
        PageDTO pageDTO = pageMapper.toDto(page);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pageDTO.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Page in the database
        List<PageEntity> pageList = pageRepository.findAll();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPage() throws Exception {
        int databaseSizeBeforeUpdate = pageRepository.findAll().size();
        page.setId(count.incrementAndGet());

        // Create the Page
        PageDTO pageDTO = pageMapper.toDto(page);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Page in the database
        List<PageEntity> pageList = pageRepository.findAll();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPage() throws Exception {
        int databaseSizeBeforeUpdate = pageRepository.findAll().size();
        page.setId(count.incrementAndGet());

        // Create the Page
        PageDTO pageDTO = pageMapper.toDto(page);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPageMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(pageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Page in the database
        List<PageEntity> pageList = pageRepository.findAll();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePage() throws Exception {
        // Initialize the database
        pageRepository.saveAndFlush(page);

        int databaseSizeBeforeDelete = pageRepository.findAll().size();

        // Delete the page
        restPageMockMvc
            .perform(delete(ENTITY_API_URL_ID, page.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<PageEntity> pageList = pageRepository.findAll();
        assertThat(pageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
