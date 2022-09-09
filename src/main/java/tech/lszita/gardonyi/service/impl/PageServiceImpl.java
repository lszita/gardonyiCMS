package tech.lszita.gardonyi.service.impl;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.lszita.gardonyi.domain.PageEntity;
import tech.lszita.gardonyi.repository.PageRepository;
import tech.lszita.gardonyi.service.PageService;
import tech.lszita.gardonyi.service.dto.PageDTO;
import tech.lszita.gardonyi.service.mapper.PageMapper;

/**
 * Service Implementation for managing {@link PageEntity}.
 */
@Service
@Transactional
public class PageServiceImpl implements PageService {

    private final Logger log = LoggerFactory.getLogger(PageServiceImpl.class);

    private final PageRepository pageRepository;

    private final PageMapper pageMapper;

    public PageServiceImpl(PageRepository pageRepository, PageMapper pageMapper) {
        this.pageRepository = pageRepository;
        this.pageMapper = pageMapper;
    }

    @Override
    public PageDTO save(PageDTO pageDTO) {
        log.debug("Request to save Page : {}", pageDTO);
        PageEntity page = pageMapper.toEntity(pageDTO);
        page = pageRepository.save(page);
        return pageMapper.toDto(page);
    }

    @Override
    public PageDTO update(PageDTO pageDTO) {
        log.debug("Request to update Page : {}", pageDTO);
        PageEntity page = pageMapper.toEntity(pageDTO);
        page = pageRepository.save(page);
        return pageMapper.toDto(page);
    }

    @Override
    public Optional<PageDTO> partialUpdate(PageDTO pageDTO) {
        log.debug("Request to partially update Page : {}", pageDTO);

        return pageRepository
            .findById(pageDTO.getId())
            .map(existingPage -> {
                pageMapper.partialUpdate(existingPage, pageDTO);

                return existingPage;
            })
            .map(pageRepository::save)
            .map(pageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PageDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Pages");
        return pageRepository.findAll(pageable).map(pageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PageDTO> findOne(Long id) {
        log.debug("Request to get Page : {}", id);
        return pageRepository.findById(id).map(pageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Page : {}", id);
        pageRepository.deleteById(id);
    }
}
