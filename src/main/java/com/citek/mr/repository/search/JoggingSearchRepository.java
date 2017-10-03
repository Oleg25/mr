package com.citek.mr.repository.search;

import com.citek.mr.domain.Jogging;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Jogging entity.
 */
public interface JoggingSearchRepository extends ElasticsearchRepository<Jogging, Long> {
}
