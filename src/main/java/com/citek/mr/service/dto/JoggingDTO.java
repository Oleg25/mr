package com.citek.mr.service.dto;


import java.time.ZonedDateTime;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Jogging entity.
 */
public class JoggingDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3)
    private String name;

    private ZonedDateTime start;

    private ZonedDateTime finish;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getFinish() {
        return finish;
    }

    public void setFinish(ZonedDateTime finish) {
        this.finish = finish;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        JoggingDTO joggingDTO = (JoggingDTO) o;
        if(joggingDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), joggingDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "JoggingDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", start='" + getStart() + "'" +
            ", finish='" + getFinish() + "'" +
            "}";
    }
}
