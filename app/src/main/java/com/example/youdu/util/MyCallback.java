package com.example.youdu.util;

public interface MyCallback<T> {
    void onSuccess(T t);
    void onFailure(int code);
}
