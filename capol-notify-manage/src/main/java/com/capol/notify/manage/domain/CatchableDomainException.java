package com.capol.notify.manage.domain;


/**
 * 需要捕获的领域异常
 *
 * @author heyong1
 * @since 2023-04-17
 */
public class CatchableDomainException extends Exception {

    private final EnumExceptionCode exceptionCode;
    private final Object[] args;

    public CatchableDomainException(EnumExceptionCode exceptionCode, Object... args) {
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public CatchableDomainException(String message, EnumExceptionCode exceptionCode, Object... args) {
        super(message);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public CatchableDomainException(String message, Throwable cause, EnumExceptionCode exceptionCode, Object... args) {
        super(message, cause);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public CatchableDomainException(Throwable cause, EnumExceptionCode exceptionCode, Object... args) {
        super(cause);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public CatchableDomainException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
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
}