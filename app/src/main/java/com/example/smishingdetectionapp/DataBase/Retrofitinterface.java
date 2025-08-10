package com.example.smishingdetectionapp.DataBase;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Retrofitinterface {

        @POST("api/auth/login-password")
        Call<LoginResponse> loginPassword(@Body Map<String, String> body);

        @POST("api/auth/login-pin")
        Call<LoginResponse> loginPin(@Body Map<String, String> body);

        // ADD THIS so EmailVerify.java can compile:
        @POST("api/auth/signup")
        Call<SignupResponse> executeSignup(@Body HashMap<String, String> map);
}