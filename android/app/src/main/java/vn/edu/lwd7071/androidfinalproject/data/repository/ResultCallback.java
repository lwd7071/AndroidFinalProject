package vn.edu.lwd7071.androidfinalproject.data.repository;

import vn.edu.lwd7071.androidfinalproject.core.ApiResult;

@FunctionalInterface
public interface ResultCallback<T> {
    void onResult(ApiResult<T> result);
}
