package com.capol.notify.manage.application;


import com.capol.notify.manage.domain.EnumExceptionCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

/**
 * 应用程序异常类
 *
 * @author heyong
 * @since 2023-04-17
 */
public class ApplicationException extends RuntimeException {

    private final EnumExceptionCode exceptionCode;
    private final Object[] args;

    public ApplicationException(EnumExceptionCode exceptionCode, Object... args) {
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public ApplicationException(String message, EnumExceptionCode exceptionCode, Object... args) {
        super(message);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public ApplicationException(String message, Throwable cause, EnumExceptionCode exceptionCode, Object... args) {
        super(message, cause);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public ApplicationException(Throwable cause, EnumExceptionCode exceptionCode, Object... args) {
        super(cause);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public ApplicationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
                                EnumExceptionCode exceptionCode, Object... args) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.exceptionCode = exceptionCode;
        this.args = args;
    }

    public EnumExceptionCode getExceptionCode() {
        return exceptionCode;
    }

    public HttpStatus httpStatus() {
        return exceptionCode.httpStatus();
    }

    public String getCode() {
        return exceptionCode.getCode();
    }

    public Object[] getArgs() {
        return args;
    }

    @Override
    public String getMessage() {
        return StringUtils.isBlank(super.getMessage()) ? exceptionCode.getCode() : super.getMessage();
    }
}