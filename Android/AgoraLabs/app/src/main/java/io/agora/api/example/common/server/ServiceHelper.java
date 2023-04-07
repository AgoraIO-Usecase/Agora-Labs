package io.agora.api.example.common.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceHelper {
    private volatile static ServiceHelper instance;
    private final Service service;
    private ServiceHelper() {
        Gson gson = new GsonBuilder()
            .setLenient()
            .create();
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://report-ad.agoralab.co/v1/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();
        service = retrofit.create(Service.class);
    }

    public static ServiceHelper getInstance() {
        if (instance == null) {
            synchronized (ServiceHelper.class) {
                if (instance == null) {
                    instance = new ServiceHelper();
                }
            }
        }
        return instance;
    }

    public Service getService() {
        return service;
    }
}
