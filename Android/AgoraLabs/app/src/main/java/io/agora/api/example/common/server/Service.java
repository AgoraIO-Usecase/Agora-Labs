package io.agora.api.example.common.server;

import io.agora.api.example.common.server.model.BaseResponse;
import io.agora.api.example.common.server.model.User;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {
    @GET("/api-login/users/verificationCode")
    Observable<BaseResponse<String>> requestSendVerCode(@Query("phone") String phone);

    @GET("/api-login/users/login")
    Observable<BaseResponse<User>> requestLogin(@Query("phone") String phone, @Query("code") String code);

    @GET("/api-login/users/cancellation")
    Observable<BaseResponse<String>> requestCancellation(@Query("userNo") String userNo);
}
