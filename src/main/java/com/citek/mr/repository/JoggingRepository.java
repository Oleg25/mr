package com.citek.mr.repository;

import com.citek.mr.domain.Jogging;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Jogging entity.
 */
@SuppressWarnings("unused")
@Repository
public interface JoggingRepository extends JpaRepository<Jogging, Long>, JpaSpecificationExecutor<Jogging> {

}
