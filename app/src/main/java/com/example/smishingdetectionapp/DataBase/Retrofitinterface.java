package com.example.smishingdetectionapp.DataBase;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Retrofitinterface {
        // TODO: need to test the login with pin with live backend server as it does not mention any backend route for login with pin so i created , running perfectly in local

        @POST("auth/login")
        Call<LoginResponse> loginPassword(@Body Map<String, String> body);          // login   email + password

        @POST("auth/login-pin")
        Call<LoginResponse> loginPin(@Body Map<String, String> body);               // login  email + pin

        @POST("auth/signup")
        Call<SignupResponse> executeSignup(@Body HashMap<String, String> map);      // sign up
}