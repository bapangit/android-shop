package com.bapan.localproducts;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    
    private static RetrofitClient retrofitClient;
    private static Retrofit retrofit;
    
    private RetrofitClient(){
        /*final OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();*/
                
        retrofit = new Retrofit.Builder()
        .baseUrl(MyUtils.BASE_URL)
        //.client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    }
    public static synchronized RetrofitClient getInstance(){
        if(retrofitClient == null){
            retrofitClient = new RetrofitClient();
        }
        return retrofitClient;
    }
    public Api getApi(){
    return retrofit.create(Api.class);
    }
}
