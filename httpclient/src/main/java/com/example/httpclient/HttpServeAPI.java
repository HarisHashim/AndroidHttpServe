package com.example.httpclient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface HttpServeAPI {

//    String BASE_URL = "http://10.0.2.2:3030";
String BASE_URL = "http://localhost:8080";

    @GET("items/")
    Call<List<Item>> getItem();

    @GET("items/{id}")
    Call<Item> getItem(@Path("id") int id);


    @GET("items")
    Call<List<Item>> findItem(@Query("name") String name, @Query("id") String id );

    @POST("items/")
    Call<Void>  postItem(@Body Item item);



}
