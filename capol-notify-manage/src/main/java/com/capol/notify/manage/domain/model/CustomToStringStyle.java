package com.capol.notify.manage.domain.model;

import org.apache.commons.lang3.builder.ToStringStyle;

public class CustomToStringStyle extends ToStringStyle {

    public CustomToStringStyle(boolean multiline) {

        this.setUseShortClassName(true);
        this.setUseIdentityHashCode(false);
        if (multiline) {
            this.setContentStart(" {");
            this.setFieldSeparator(System.lineSeparator() + "  ");
            this.setFieldSeparatorAtStart(true);
            this.setContentEnd(System.lineSeparator() + "}");
        } else {
            this.setContentStart("{");
            this.setFieldSeparator(", ");
            this.setContentEnd("}");
        }
    }

    public static CustomToStringStyle INLINE_INSTANCE = new CustomToStringStyle(false);
    public static CustomToStringStyle MULTILINE_INSTANCE = new CustomToStringStyle(true);
}
