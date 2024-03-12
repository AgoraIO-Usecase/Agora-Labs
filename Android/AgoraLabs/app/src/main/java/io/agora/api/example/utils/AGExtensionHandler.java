package io.agora.api.example.utils;

public interface AGExtensionHandler {

    void onStart(String provider, String ext);

    void onStop(String provider, String ext);

    void onEvent(String provider, String ext, String key, String msg);

    void onError(String provider, String ext, int key, String msg);
}