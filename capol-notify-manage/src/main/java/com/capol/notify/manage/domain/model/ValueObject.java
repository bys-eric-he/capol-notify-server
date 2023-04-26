package com.capol.notify.manage.domain.model;


import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import java.io.Serializable;

/**
 * 值对象
 */
public class ValueObject implements Serializable {

    public static <T extends Comparable<T>> T max(T a, T b) {
        if (a.compareTo(b) > 0) {
            return a;
        }
        return b;
    }

    public static <T extends Comparable<T>> T min(T a, T b) {
        if (a.compareTo(b) < 0) {
            return a;
        }
        return b;
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, CustomToStringStyle.INLINE_INSTANCE,
                false, false, true, ValueObject.class);
    }
}