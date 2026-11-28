package vn.edu.lwd7071.androidfinalproject.core;

public interface UiState<T> {
    record Loading<T>() implements UiState<T> {}
    record Content<T>(T data) implements UiState<T> {}
    record Refreshing<T>(T data) implements UiState<T> {}
    record Stale<T>(T data, String message) implements UiState<T> {}
    record Error<T>(String message) implements UiState<T> {}
}
