package com.example.miniprojet;

import java.util.List;

public interface FetchState<T> {
    void onSuccess(List<T> data);
    void onError(Exception e);
}
