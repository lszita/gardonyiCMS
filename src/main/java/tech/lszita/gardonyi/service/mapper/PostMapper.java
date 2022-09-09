package tech.lszita.gardonyi.service.mapper;

import org.mapstruct.*;
import tech.lszita.gardonyi.domain.Post;
import tech.lszita.gardonyi.service.dto.PostDTO;

/**
 * Mapper for the entity {@link Post} and its DTO {@link PostDTO}.
 */
@Mapper(componentModel = "spring")
public interface PostMapper extends EntityMapper<PostDTO, Post> {}
