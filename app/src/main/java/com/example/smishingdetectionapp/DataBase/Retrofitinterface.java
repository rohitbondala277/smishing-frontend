package com.example.smishingdetectionapp.DataBase;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Retrofitinterface {

        // Unified login: send email + (password OR pin)
        @POST("api/auth/login")
        Call<LoginResponse> login(@Body Map<String, String> body);

        @POST("api/auth/signup")
        Call<SignupResponse> executeSignup(@Body HashMap<String, String> map);

        @POST("api/auth/checkemail")
        Call<SignupResponse> checkEmail(@Body HashMap<String, String> map);

}