package com.example.smishingdetectionapp.DataBase;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Retrofitinterface {

    @POST("auth/login")
    Call<DBresult> executeLogin(@Body HashMap<String, String> map);

    @POST("auth/signup")
    Call<SignupResponse> executeSignup(@Body HashMap<String, String> map);

    @POST("auth/verify-email")
    Call<SignupResponse> verifyEmail(@Body HashMap<String, String> map);
}
