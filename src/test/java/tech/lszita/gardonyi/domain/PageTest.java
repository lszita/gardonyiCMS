package tech.lszita.gardonyi.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import tech.lszita.gardonyi.web.rest.TestUtil;

class PageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PageEntity.class);
        PageEntity page1 = new PageEntity();
        page1.setId(1L);
        PageEntity page2 = new PageEntity();
        page2.setId(page1.getId());
        assertThat(page1).isEqualTo(page2);
        page2.setId(2L);
        assertThat(page1).isNotEqualTo(page2);
        page1.setId(null);
        assertThat(page1).isNotEqualTo(page2);
    }
}
