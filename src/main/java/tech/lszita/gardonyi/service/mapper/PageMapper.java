package tech.lszita.gardonyi.service.mapper;

import org.mapstruct.*;
import tech.lszita.gardonyi.domain.PageEntity;
import tech.lszita.gardonyi.service.dto.PageDTO;

/**
 * Mapper for the entity {@link PageEntity} and its DTO {@link PageDTO}.
 */
@Mapper(componentModel = "spring")
public interface PageMapper extends EntityMapper<PageDTO, PageEntity> {}
