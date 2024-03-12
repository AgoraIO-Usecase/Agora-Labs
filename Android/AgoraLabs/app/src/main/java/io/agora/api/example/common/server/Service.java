package io.agora.api.example.common.server;

import io.agora.api.example.common.server.model.BaseResponse;
import io.agora.api.example.common.server.model.ReportResponseData;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Service {
    @POST("report")
    @Headers({"Content-Type: application/json"})
    Observable<BaseResponse<ReportResponseData>> report(@Body RequestBody body);


}
