package com.citek.mr.service.mapper;

import com.citek.mr.domain.*;
import com.citek.mr.service.dto.JoggingDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Jogging and its DTO JoggingDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface JoggingMapper extends EntityMapper <JoggingDTO, Jogging> {
    
    
    default Jogging fromId(Long id) {
        if (id == null) {
            return null;
        }
        Jogging jogging = new Jogging();
        jogging.setId(id);
        return jogging;
    }
}
