package vn.edu.lwd7071.androidfinalproject.core;

public interface ApiResult<T> {
    record Success<T>(T value) implements ApiResult<T> {}
    record Failure<T>(String code, String message) implements ApiResult<T> {}
}
