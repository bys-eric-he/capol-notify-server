package com.capol.notify.manage.domain.model;


import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.util.Objects;

public class AbstractId implements Serializable {

    private String id;

    protected AbstractId() {
    }

    public AbstractId(String anId) {
        Validate.notBlank(anId, "ID must not be null");
        this.id = anId;
    }

    private void setId(String anId) {
        this.id = anId;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractId that = (AbstractId) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
}