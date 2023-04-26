package com.capol.notify.manage.domain;


import org.apache.commons.lang3.StringUtils;

/**
 * 领域异常
 *
 * @author heyong1
 * @since 2023-04-17
 */
public class DomainException extends RuntimeException {

    private final EnumExceptionCode exceptionCode;
    private final Object[] args;

    public DomainException(EnumExceptionCode exceptionCode, Object... args) {
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public DomainException(String message, EnumExceptionCode exceptionCode, Object... args) {
        super(message);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public DomainException(String message, Throwable cause, EnumExceptionCode exceptionCode, Object... args) {
        super(message, cause);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public DomainException(Throwable cause, EnumExceptionCode exceptionCode, Object... args) {
        super(cause);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public DomainException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
                           EnumExceptionCode exceptionCode, Object... args) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public EnumExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String getMessage() {
        return StringUtils.isBlank(super.getMessage()) ? exceptionCode.getCode() : super.getMessage();
    }
}