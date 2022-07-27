package com.dongblog.dongblog.api.exception;

/**
 * status -> 404
 */
public class PostNotFound extends DongBlogException {

    private static final String MESSAGE = "존재하지 않는 글입니다.";

    public PostNotFound() {
        super(MESSAGE);
    }

    public PostNotFound(Throwable cause) {
        super(MESSAGE, cause);
    }

    public int getStatusCode() {
        return 404;
    }
}
