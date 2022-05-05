package com.bapan.localproducts;

import com.bapan.localproducts.models.FetchProductResponse;
import com.bapan.localproducts.models.CommonResponse;
import com.bapan.localproducts.models.LoginResponse;
import com.bapan.localproducts.models.MyProductsResponse;
import com.bapan.localproducts.models.OtpResponse;
import com.bapan.localproducts.models.OtpVerifyResponse;
import com.bapan.localproducts.models.PlaceNamesResponse;
import com.bapan.localproducts.models.ProfileResponse;
import com.bapan.localproducts.models.PublisherResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {
    @FormUrlEncoded
    @POST("sendotp.php")
    Call<OtpResponse> sendOtp(@Field("phone") String phone);

    @FormUrlEncoded
    @POST("verifyotp.php")
    Call<OtpVerifyResponse> verifyOtp(@Field("phone") String phone,@Field("otp") String otp);
    
    @FormUrlEncoded
    @POST("login.php")
    Call<LoginResponse> login(@Field("id") String id, @Field("name") String name, @Field("pin") String pin, @Field("place") int place,@Field("image")String image);

    @FormUrlEncoded
    @POST("placenames.php")
    Call<PlaceNamesResponse> getPlaceNames(@Field("pin") String pin);

    @FormUrlEncoded
    @POST("profile.php")
    Call<ProfileResponse> getProfile(@Field("id") String id);

    @FormUrlEncoded
    @POST("createproduct.php")
    Call<CommonResponse> createProduct(
         @Field("id") String id,
         @Field("encodedimage") String encodedimage,
         @Field("title") String title,
         @Field("price") String price,
         @Field("details") String details,
         @Field("demandtype") String demandtype,
         @Field("published") String published
    );
    
    @FormUrlEncoded
    @POST("myproducts.php")
    Call<MyProductsResponse> getMyProducts(@Field("id") String id, @Field(("demandtype")) String demandtype);
    
    @FormUrlEncoded
    @POST("editordetails.php")
    Call<FetchProductResponse> getEditorDetails(@Field("id") String id, @Field(("timestamp")) String timestamp);

    @FormUrlEncoded
    @POST("updateproduct.php")
    Call<CommonResponse> updateProduct(
            @Field("id") String id,
            @Field("timestamp") String timestamp,
            @Field("encodedimage") String encodedimage,
            @Field("title") String title,
            @Field("price") String price,
            @Field("details") String details,
            @Field("demandtype") String demandtype,
            @Field("published") String published
            );
    @FormUrlEncoded
    @POST("products.php")
    Call<MyProductsResponse> getProducts(
            @Field("placecode") String placecode,
            @Field("timestamp") String timestamp
    );
    @FormUrlEncoded
    @POST("publisher.php")
    Call<PublisherResponse> getPublisher(@Field("usersl") String usersl);
    
    @FormUrlEncoded
    @POST("deleteproduct.php")
    Call<CommonResponse> deleteProduct(
    @Field("id") String id,
    @Field("timestamp") String timestamp
    );
}
