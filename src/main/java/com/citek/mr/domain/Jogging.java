package com.citek.mr.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * A Jogging.
 */
@Entity
@Table(name = "jogging")
@Document(indexName = "jogging")
public class Jogging implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(min = 3)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "jhi_start")
    private ZonedDateTime start;

    @Column(name = "finish")
    private ZonedDateTime finish;

    // citek-needle-entity-add-field - citek will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Jogging name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getStart() {
        return start;
    }

    public Jogging start(ZonedDateTime start) {
        this.start = start;
        return this;
    }

    public void setStart(ZonedDateTime start) {
        this.start = start;
    }

    public ZonedDateTime getFinish() {
        return finish;
    }

    public Jogging finish(ZonedDateTime finish) {
        this.finish = finish;
        return this;
    }

    public void setFinish(ZonedDateTime finish) {
        this.finish = finish;
    }
    // citek-needle-entity-add-getters-setters - citek will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Jogging jogging = (Jogging) o;
        if (jogging.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), jogging.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Jogging{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", start='" + getStart() + "'" +
            ", finish='" + getFinish() + "'" +
            "}";
    }
}
