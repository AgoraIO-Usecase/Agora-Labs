package io.agora.api.example.common.base.component;

public interface ISingleCallback<T, V> {
    void onSingleCallback(T type, V data);
}