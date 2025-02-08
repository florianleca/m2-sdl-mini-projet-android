package com.example.miniprojet;

public interface PostState<T> {

    void onSuccess(T data);
    void onError(Exception e);

}
