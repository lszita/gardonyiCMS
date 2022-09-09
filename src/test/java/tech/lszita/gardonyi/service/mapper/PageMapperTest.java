package tech.lszita.gardonyi.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PageMapperTest {

    private PageMapper pageMapper;

    @BeforeEach
    public void setUp() {
        pageMapper = new PageMapperImpl();
    }
}
