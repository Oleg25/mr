package com.citek.mr.web.rest;

import com.citek.mr.web.rest.util.ResponseUtil;
import com.codahale.metrics.annotation.Timed;
import com.citek.mr.service.JoggingService;
import com.citek.mr.web.rest.util.HeaderUtil;
import com.citek.mr.web.rest.util.PaginationUtil;
import com.citek.mr.service.dto.JoggingDTO;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Jogging.
 */
@RestController
@RequestMapping("/api")
public class JoggingResource {

    private final Logger log = LoggerFactory.getLogger(JoggingResource.class);

    private static final String ENTITY_NAME = "jogging";

    private final JoggingService joggingService;

    public JoggingResource(JoggingService joggingService) {
        this.joggingService = joggingService;
    }

    /**
     * POST  /joggings : Create a new jogging.
     *
     * @param joggingDTO the joggingDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new joggingDTO, or with status 400 (Bad Request) if the jogging has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/joggings")
    @Timed
    public ResponseEntity<JoggingDTO> createJogging(@Valid @RequestBody JoggingDTO joggingDTO) throws URISyntaxException {
        log.debug("REST request to save Jogging : {}", joggingDTO);
        if (joggingDTO.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new jogging cannot already have an ID")).body(null);
        }
        JoggingDTO result = joggingService.save(joggingDTO);
        return ResponseEntity.created(new URI("/api/joggings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /joggings : Updates an existing jogging.
     *
     * @param joggingDTO the joggingDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated joggingDTO,
     * or with status 400 (Bad Request) if the joggingDTO is not valid,
     * or with status 500 (Internal Server Error) if the joggingDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/joggings")
    @Timed
    public ResponseEntity<JoggingDTO> updateJogging(@Valid @RequestBody JoggingDTO joggingDTO) throws URISyntaxException {
        log.debug("REST request to update Jogging : {}", joggingDTO);
        if (joggingDTO.getId() == null) {
            return createJogging(joggingDTO);
        }
        JoggingDTO result = joggingService.save(joggingDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, joggingDTO.getId().toString()))
            .body(result);
    }


    /**
     * GET  /joggings/:id : get the "id" jogging.
     *
     * @param id the id of the joggingDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the joggingDTO, or with status 404 (Not Found)
     */
    @GetMapping("/joggings/{id}")
    @Timed
    public ResponseEntity<JoggingDTO> getJogging(@PathVariable Long id) {
        log.debug("REST request to get Jogging : {}", id);
        JoggingDTO joggingDTO = joggingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(joggingDTO));
    }

    /**
     * DELETE  /joggings/:id : delete the "id" jogging.
     *
     * @param id the id of the joggingDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/joggings/{id}")
    @Timed
    public ResponseEntity<Void> deleteJogging(@PathVariable Long id) {
        log.debug("REST request to delete Jogging : {}", id);
        joggingService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/joggings?query=:query : search for the jogging corresponding
     * to the query.
     *
     * @param query the query of the jogging search
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/joggings")
    @Timed
    public ResponseEntity<List<JoggingDTO>> searchJoggings(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Joggings for query {}", query);
        Page<JoggingDTO> page = joggingService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/joggings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

}
