package io.agora.api.example.common.server;

import io.agora.api.example.common.server.model.BaseResponse;
import io.agora.api.example.common.server.model.ReportResponseData;
import io.agora.api.example.common.server.model.User;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface Service {
    @POST("report")
    @Headers({"Content-Type: application/json"})
    Observable<BaseResponse<ReportResponseData>> report(@Body RequestBody body);
}
