package com.citek.mr.service;

import com.citek.mr.service.dto.JoggingDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing Jogging.
 */
public interface JoggingService {

    /**
     * Save a jogging.
     *
     * @param joggingDTO the entity to save
     * @return the persisted entity
     */
    JoggingDTO save(JoggingDTO joggingDTO);

    /**
     *  Get all the joggings.
     *
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<JoggingDTO> findAll(Pageable pageable);

    /**
     *  Get the "id" jogging.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    JoggingDTO findOne(Long id);

    /**
     *  Delete the "id" jogging.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the jogging corresponding to the query.
     *
     *  @param query the query of the search
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    Page<JoggingDTO> search(String query, Pageable pageable);
}
