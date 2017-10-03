package com.citek.mr.service.impl;

import com.citek.mr.service.JoggingService;
import com.citek.mr.domain.Jogging;
import com.citek.mr.repository.JoggingRepository;
import com.citek.mr.repository.search.JoggingSearchRepository;
import com.citek.mr.service.dto.JoggingDTO;
import com.citek.mr.service.mapper.JoggingMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Jogging.
 */
@Service
@Transactional
public class JoggingServiceImpl implements JoggingService{

    private final Logger log = LoggerFactory.getLogger(JoggingServiceImpl.class);

    private final JoggingRepository joggingRepository;

    private final JoggingMapper joggingMapper;

    private final JoggingSearchRepository joggingSearchRepository;

    public JoggingServiceImpl(JoggingRepository joggingRepository, JoggingMapper joggingMapper, JoggingSearchRepository joggingSearchRepository) {
        this.joggingRepository = joggingRepository;
        this.joggingMapper = joggingMapper;
        this.joggingSearchRepository = joggingSearchRepository;
    }

    /**
     * Save a jogging.
     *
     * @param joggingDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public JoggingDTO save(JoggingDTO joggingDTO) {
        log.debug("Request to save Jogging : {}", joggingDTO);
        Jogging jogging = joggingMapper.toEntity(joggingDTO);
        jogging = joggingRepository.save(jogging);
        JoggingDTO result = joggingMapper.toDto(jogging);
        joggingSearchRepository.save(jogging);
        return result;
    }

    /**
     *  Get all the joggings.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<JoggingDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Joggings");
        return joggingRepository.findAll(pageable)
            .map(joggingMapper::toDto);
    }

    /**
     *  Get one jogging by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public JoggingDTO findOne(Long id) {
        log.debug("Request to get Jogging : {}", id);
        Jogging jogging = joggingRepository.findOne(id);
        return joggingMapper.toDto(jogging);
    }

    /**
     *  Delete the  jogging by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Jogging : {}", id);
        joggingRepository.delete(id);
        joggingSearchRepository.delete(id);
    }

    /**
     * Search for the jogging corresponding to the query.
     *
     *  @param query the query of the search
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<JoggingDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Joggings for query {}", query);
        Page<Jogging> result = joggingSearchRepository.search(queryStringQuery(query), pageable);
        return result.map(joggingMapper::toDto);
    }
}
